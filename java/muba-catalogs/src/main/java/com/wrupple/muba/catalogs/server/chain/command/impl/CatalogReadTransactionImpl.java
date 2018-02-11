package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogPluginQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
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
    private final CatalogKeyServices keyDelegate;

    private final FieldAccessStrategy access;

    private final EntrySynthesizer synthesizer;

    private final CompleteCatalogGraph graphJoin;

    private final CatalogReaderInterceptor queryRewriter;

    private final ExplicitDataJoin join;

    // DataReadCommandImpl
    private final PrimaryKeyReaders primaryKeyers;
    // DataQueryCommandImpl
    private final QueryReaders queryers;

    private int MIN_TREE_LEVELS;

    @Inject
    public CatalogReadTransactionImpl(CatalogPluginQueryCommand pluginStorage, @com.google.inject.name.Named("catalog.storage.metadata") String catalogPluginStorage, CatalogKeyServices keyDelegate, FieldAccessStrategy access, EntrySynthesizer synthesizer, @Named("catalog.read.preloadCatalogGraph") Integer minLevelsDeepOfhierarchy,
                                      QueryReaders queryers, PrimaryKeyReaders primaryKeyers, CompleteCatalogGraph graphJoin,
                                      ExplicitDataJoin join, CatalogReaderInterceptor queryRewriter) {
        this.keyDelegate = keyDelegate;
        this.access = access;
        this.synthesizer = synthesizer;


        this.graphJoin = graphJoin;
        this.join = join;
        this.MIN_TREE_LEVELS = minLevelsDeepOfhierarchy;
        this.queryers = queryers;
        this.primaryKeyers = primaryKeyers;
        this.queryRewriter = queryRewriter;
        this.queryers.addCommand(catalogPluginStorage,pluginStorage);
        primaryKeyers.addCommand(catalogPluginStorage,pluginStorage);

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
        CatalogDescriptor catalog = context.getCatalogDescriptor();

        Instrospection instrospection = access.newSession(null);

        CatalogResultCache cache = context.getCache(context.getCatalogDescriptor(), context);

        if (targetEntryId == null) {
            applySorts(filter, catalog.getAppliedSorts());
            applyCriteria(filter, catalog, catalog.getAppliedCriteria(), context, instrospection);
            List<CatalogEntry> result = null;
            if(filter==null && CatalogDescriptor.CATALOG_ID.equals(catalog.getDistinguishedName())){
                log.warn("Non filtered read of catalog list will result in all catalogs");
                filter = FilterDataUtils.newFilterData();
                filter.setConstrained(false);
                context.getRequest().setFollowReferences(false);
            }
            if(filter==null){
                throw new IllegalArgumentException("No entry or filters provided");
            }
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
        if (keyDelegate.isPrimaryKey(vanityId)) {
            // almost certainly an Id
            try {
                Object primaryId = keyDelegate.decodePrimaryKeyToken(vanityId);
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
            FieldDescriptor dn = catalog.getFieldDescriptor(HasDistinguishedName.FIELD);
            if (dn != null) {
                if(dn.isFilterable()){

                    FilterData filter = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, vanityId);
                    List<CatalogEntry> results = queryUnits(filter, catalog, context, instrospection);
                    if (results == null || results.isEmpty()) {
                        log.error("attempt to use {} as discriminator returned no results", vanityId);
                    } else {
                        if(CatalogDescriptor.CATALOG_ID.equals(catalog.getDistinguishedName())){
                            log.warn("vanity id query for a catalog descriptor return a result, will force to fully build result graph");
                        }
                        context.getRequest().setFollowReferences(true);
                        regreso = results.get(0);
                    }
                }else{
                    log.error("{} is not a filterable field",HasDistinguishedName.FIELD);
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

        if (synthesizer.evaluateGreatAncestor(context,catalog,null) != null && !catalog.getConsolidated()) {

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

        if (synthesizer.evaluateGreatAncestor(context,catalog,null) != null && !catalog.getConsolidated()) {
            // read child entity
            CatalogEntry childEntity = context.getEntryResult();
            // we are certain this catalog has a parent, otherwise this DAO
            // would
            // not be called
            Long parentCatalogId = catalog.getParent();
            // aquire parent id
            Object parentEntityId = synthesizer.getAllegedParentId(childEntity, instrospection,access);
            // delegate deeper getInheritance to another instance of an
            // AncestorAware
            // DAO

            log.trace("[PROCESSING CHILD ENTRY]");
            synthesizer.processChildInheritance(childEntity,
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
            parentEntityId = synthesizer.getAllegedParentId(childEntity, instrospection,access);
            synthesizer.processChildInheritance(childEntity, parent, parentEntityId, context, catalog,
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
                    Object criteriaValue = synthesizer.synthethizeFieldValue(((String) criteria.getValue()).split(" "), context);
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
