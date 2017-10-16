package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.domain.impl.CatalogUserTransactionImpl;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class CatalogReadTransactionImpl  implements CatalogReadTransaction {

    protected static final Logger log = LoggerFactory.getLogger(CatalogReadTransactionImpl.class);

    public interface JoinCondition {
        boolean match(CatalogEntry o);
    }


    private final CompleteCatalogGraph graphJoin;

    private final CatalogReaderInterceptor queryRewriter;

    private final ExplicitDataJoin join;

    // DataReadCommandImpl
    private final PrimaryKeyReaders primaryKeyers;
    // DataQueryCommandImpl
    private final QueryReaders queryers;

    private int MIN_TREE_LEVELS;

    @Inject
    public CatalogReadTransactionImpl(CatalogPluginQueryCommand pluginStorage, @com.google.inject.name.Named("catalog.metadata.storage") String catalogPluginStorage, @Named("catalog.read.preloadCatalogGraph") Integer minLevelsDeepOfhierarchy,
                                      QueryReaders queryers, PrimaryKeyReaders primaryKeyers, CompleteCatalogGraph graphJoin,
                                      ExplicitDataJoin join, CatalogReaderInterceptor queryRewriter) {


        this.graphJoin = graphJoin;
        this.join = join;
        this.MIN_TREE_LEVELS = minLevelsDeepOfhierarchy;
        this.queryers = queryers;
        this.primaryKeyers = primaryKeyers;
        this.queryRewriter = queryRewriter;
        this.queryers.addCommand(catalogPluginStorage,pluginStorage);

    }

    /*
     * READING ACTION (lucky!)?
     */
    @Override
    public boolean execute(Context x) throws Exception {
        log.trace("[START READ]");
        CatalogActionContext context = (CatalogActionContext) x;
        Object targetEntryId = context.getRequest().getEntry();
        FilterData filter = context.getRequest().getFilter();
        log.info("[Resolving catalog metadata]");
        CatalogDescriptor catalog = context.getCatalogDescriptor();

        Instrospection instrospection = context.getCatalogManager().access().newSession(null);

        CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);

        if (targetEntryId == null) {
            applySorts(filter, catalog.getAppliedSorts());
            applyCriteria(filter, catalog, catalog.getAppliedCriteria(), context, instrospection);
            List<CatalogEntry> result = null;
            FilterCriteria keyCriteria = filter.fetchCriteria(catalog.getKeyField());
            if (!filter.isConstrained() || keyCriteria == null) {
                result = read(filter, catalog, context, cache, instrospection);
            } else {
                List<Object> keys = keyCriteria.getValues();
                if (keys == null) {
                    context.getRuntimeContext().addWarning("malformed criteria");
                } else {

                    if (filter.getCursor() == null) {
                        int ammountOfKeys = keys.size();
                        // only if theres still some unsatisfied idś in criteria
                        if (filter.getStart() < ammountOfKeys) {
                            result = read(filter, catalog, context, cache, instrospection);
                        }
                    } else {
                        result = read(filter, catalog, context, cache, instrospection);
                    }
                }
            }
            if(result==null){
                log.trace("[RESULT ] null");
            }else {
                if(result.size()> FilterData.DEFAULT_INCREMENT){
                    log.trace("[RESULT SIZE] {}", result.size());
                }else{
                    log.trace("[RESULT] {}", result);
                }
                for(CatalogEntry dafsgf:result){

                    //FIXME batch
                    queryRewriter.interceptResult(dafsgf, context, catalog);

                }
                if(result.isEmpty()){
                    result = null;
                }
            }


            context.setResults(result);
            if(result!=null&&catalog.getDistinguishedName().equals(CatalogDescriptor.CATALOG_ID)){
                List<CatalogDescriptor> descriptors= (List)result;
                for(CatalogDescriptor armado: descriptors){
                    log.warn("[incomplete metadata] {}",armado);
                    context.getRuntimeContext().getRootAncestor().put(armado.getDistinguishedName()+ CatalogActionContext.INCOMPLETO,armado);

                }
              }

            String[][] joins = filter.getJoins();
            if (joins != null && joins.length > 0) {
                join.execute(context);
            } else if (MIN_TREE_LEVELS > 0 || context.getRequest().getFollowReferences()) {// interceptor
                // decides
                // to
                // read
                // graph
                graphJoin.execute(context);
            }
        } else {
            CatalogEntry originalEntry=readTargetEntryId(instrospection,cache,catalog,targetEntryId,context);

            if(originalEntry==null){

                context.setResults(null);

            }else{
                if(catalog.getDistinguishedName().equals(CatalogDescriptor.CATALOG_ID)){
                    CatalogDescriptor armado = (CatalogDescriptor) originalEntry;
                    log.warn("[incomplete metadata] {}",armado);

                    context.getRuntimeContext().getRootAncestor().put(armado.getDistinguishedName()+ CatalogActionContext.INCOMPLETO,armado);
                }
                queryRewriter.interceptResult(originalEntry, context, catalog);
                context.setResults(Collections.singletonList(originalEntry));

                if (context.getRequest().getFollowReferences()) {
                    graphJoin.execute(context);
                }
            }
            log.trace("[RESULT ] {}", originalEntry);
        }


        return CONTINUE_PROCESSING;
    }

    private CatalogEntry readTargetEntryId(Instrospection instrospection,CatalogResultCache cache,CatalogDescriptor catalog,Object targetEntryId, CatalogActionContext context) throws Exception {

        if (targetEntryId instanceof String) {
            return readVanityId((String) targetEntryId, catalog, context, cache, instrospection);

        } else {
            return read(targetEntryId, catalog, context, cache, instrospection);
        }
    }

    public List<CatalogEntry> read(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
                                   CatalogResultCache cache, Instrospection instrospection) throws Exception {
        List<CatalogEntry> regreso;
        filterData = queryRewriter.interceptQuery(filterData, context, catalog);

        if (cache == null) {
            regreso = queryUnits(filterData, catalog, context, instrospection);
        } else {
            regreso = cache.satisfy(context, catalog.getDistinguishedName(), filterData);
            if (regreso == null) {
                regreso = queryUnits(filterData, catalog, context, instrospection);
                if (regreso != null) {
                    if (log.isInfoEnabled()) {
                        log.trace("Saving {} query result(s) in cache {} list {}", regreso.size(),
                                catalog.getDistinguishedName(), filterData);
                    }
                    cache.put(context, catalog.getDistinguishedName(), regreso, filterData);
                }
            }
        }

        context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
                null/* no undo */);

        return regreso;
    }

    public CatalogEntry read(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context, CatalogResultCache cache, Instrospection instrospection) throws Exception {
        CatalogEntry regreso;
        if (cache == null) {
            regreso = doRead(targetEntryId, catalog, context, instrospection);
        } else {
            regreso = cache.get(context, catalog.getDistinguishedName(), targetEntryId);
            if (regreso == null) {
                regreso = doRead(targetEntryId, catalog, context, instrospection);
                if (regreso != null) {
                    cache.put(context, catalog.getDistinguishedName(), regreso);
                }
            }
        }
        context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
                null/* no undo */);
        return regreso;
    }

    private CatalogEntry doRead(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
        List<String> storageUnits = catalog.getStorage();

        Command storageUnit;
        if (storageUnits == null ||storageUnits.isEmpty() ||storageUnits.size() == 1) {
            if (storageUnits == null || storageUnits.isEmpty()) {
                //default?
                storageUnit = primaryKeyers.getDefault();
            } else {
                storageUnit = primaryKeyers.getCommand(storageUnits.get(0));
            }
            return doRead(targetEntryId,catalog,context,instrospection,storageUnit);
        } else {
            //first to preset results wins
            CatalogEntry result;
            for (String storageName : storageUnits) {
                storageUnit = primaryKeyers.getCommand(storageName);

                result =  doRead(targetEntryId,catalog,context,instrospection,storageUnit);
                if (result != null) {
                    return result;
                }
            }
            return null;


        }
    }

    public CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogActionContext context,
                                     CatalogResultCache cache, Instrospection instrospection) throws Exception {
        CatalogEntry regreso = null;
        if (context.getCatalogManager().isPrimaryKey(vanityId)) {
            // almost certainly an Id
            try {
                Object primaryId = context.getCatalogManager().decodePrimaryKeyToken(vanityId);
                if(primaryId!=vanityId || catalog.getFieldDescriptor(catalog.getKeyField()).getDataType()==CatalogEntry.STRING_DATA_TYPE){
                    //FIXME in vanity id cases this query is repeated each call before querying distinguished name. Results should be cached at a higher level to catch vanityId results
                    regreso = read(primaryId, catalog, context, cache, instrospection);
                }

            } catch (NumberFormatException e) {
                log.warn("specified parameter {} could not be used as a primary key", vanityId);
            }

        }

        if (regreso == null) {
            log.info("primary key {} returned no results", vanityId);
            if (catalog.getFieldDescriptor(HasDistinguishedName.FIELD) != null) {
                FilterData filter = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, vanityId);
                List<CatalogEntry> results = queryUnits(filter, catalog, context, instrospection);
                if (results == null || results.isEmpty()) {
                    log.error("attempt to use {} as discriminator returned no results", vanityId);
                } else {
                    regreso = results.get(0);
                }
            }

        }
        return regreso;
    }

    private List<CatalogEntry> queryUnits(FilterData filter, CatalogDescriptor catalog, CatalogActionContext context, Instrospection instrospection) throws Exception {

        List<String> storageUnits = catalog.getStorage();

        Command storageUnit;
        if (storageUnits == null || storageUnits.size() == 1) {
            if (storageUnits == null || storageUnits.isEmpty()) {
                //default?
                storageUnit = queryers.getDefault();
            } else {
                storageUnit = queryers.getCommand(storageUnits.get(0));
            }
            return queryUnits(filter, catalog, context, instrospection, storageUnit);
        } else {

            Integer storageStrategy = catalog.getStorageStrategy()==null?0:catalog.getStorageStrategy();
            switch (storageStrategy) {
                case 1:
                    final  List<CatalogEntry> results = new LinkedList<>();
                    //append
                    storageUnits.stream().
                            map(unitName -> queryers.getCommand(unitName)).
                            map(command -> {
                                try {
                                    return queryUnits(filter, catalog, context, instrospection, command);
                                } catch (Exception e) {
                                    log.error("Storage unit " + command, e);
                                    return null;
                                }
                            }).
                            forEach(partialResults->{
                                if(partialResults!=null  && ! partialResults.isEmpty())
                                    results.addAll(partialResults);
                            });
                        return results.isEmpty() ? null : results;
                case 0:
                default:
                    //first to preset results wins
                    List<CatalogEntry> patials;
                    for (String storageName : storageUnits) {
                        storageUnit = queryers.getCommand(storageName);
                        log.debug("[Querying unit] {}",storageUnit);
                        patials = queryUnits(filter, catalog, context, instrospection, storageUnit);
                        if (patials != null && ! patials.isEmpty()) {
                            return patials;
                        }
                    }
                    return null;

            }


        }

    }

    private List<CatalogEntry> queryUnits(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
                                          Instrospection instrospection, Command storageUnit) throws Exception {
        log.trace("DATASTORE QUERY");
        context.getRequest().setFilter(filterData);

        storageUnit.execute(context);

        if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {

            // read child Results
            List<CatalogEntry> children = context.getResults();
            if (children != null && !children.isEmpty()) {
                processChildren(children, context, catalog, instrospection);
                return children;
            } else {
                return Collections.EMPTY_LIST;
            }
        }

        return context.getResults();
    }

    private CatalogEntry doRead(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
                                Instrospection instrospection, Command storageUnit) throws Exception {
        log.trace("DATASTORE READ {}",targetEntryId);
        context.getRequest().setEntry(targetEntryId);


        storageUnit.execute(context);

        if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
            // read child entity
            CatalogEntry childEntity = context.getEntryResult();
            // we are certain this catalog has a parent, otherwise this DAO
            // would
            // not be called
            Long parentCatalogId = catalog.getParent();
            // aquire parent id
            Object parentEntityId = context.getCatalogManager().getAllegedParentId(childEntity, instrospection);
            // delegate deeper inheritance to another instance of an
            // AncestorAware
            // DAO

            log.trace("[PROCESSING CHILD ENTRY]");
            context.getCatalogManager().processChild(childEntity,
                    context.getDescriptorForKey(parentCatalogId), parentEntityId,
                    context, catalog, instrospection);
        }
        return context.getEntryResult();

    }

    protected void processChildren(List<CatalogEntry> children, CatalogActionContext context,
                                   CatalogDescriptor catalog, Instrospection instrospection) throws Exception {
        // we are certain this catalog has a parent, otherwise this DAO would
        // not be called
        Long parentCatalogId = catalog.getParent();
        CatalogDescriptor parent = context.getDescriptorForKey(parentCatalogId);
        Object parentEntityId;
        for (CatalogEntry childEntity : children) {
            parentEntityId = context.getCatalogManager().getAllegedParentId(childEntity, instrospection);
            context.getCatalogManager().processChild(childEntity, parent, parentEntityId, context, catalog,
                    instrospection);
        }
    }

    private void applyCriteria(FilterData filter, CatalogDescriptor catalog,
                               List<? extends FilterCriteria> appliedCriteria, CatalogActionContext context, Instrospection instrospection)
            throws Exception {
        if (appliedCriteria != null) {
            for (FilterCriteria criteria : appliedCriteria) {
                if (criteria.getEvaluate()) {
                    String operator = criteria.getOperator();
                    Object criteriaValue = context.getCatalogManager()
                            .synthethizeFieldValue(((String) criteria.getValue()).split(" "), context);
                    criteria = FilterDataUtils.createSingleFieldFilter(criteria.getPath(), criteriaValue);
                    criteria.setOperator(operator);
                }
                filter.addFilter(criteria);
            }
        }
    }

    private void applySorts(FilterData filter, List<FilterDataOrdering> appliedSorts) {
        if (appliedSorts != null) {
            for (FilterDataOrdering ordering : appliedSorts) {
                filter.addOrdering(ordering);
            }
        }
    }

	/*
     * FIXME public static String buildVanityToken(HasDistinguishedName task) {
	 * 
	 * String name = task.getDistinguishedName(); if (name == null) { name =
	 * task.getName(); if (name == null) { name = task.getIdAsString(); } else {
	 * //[^a-zA-Z0-9/] replace all except / name =
	 * name.replaceAll("[^a-zA-Z0-9]", "-"); } }
	 * 
	 * return name; }
	 * 
	 * public static String getNameFromVanityToken(String vanityToken){ return
	 * vanityToken.replace('-',' '); }
	 */

}
