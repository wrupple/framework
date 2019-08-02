package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class CatalogReadTransactionImpl  implements CatalogReadTransaction {

    protected static final Logger log = LogManager.getLogger(CatalogReadTransactionImpl.class);

    private final CatalogKeyServices keyDelegate;

    private final FieldAccessStrategy access;

    private final EntrySynthesizer synthesizer;

    private final FieldSynthesizer fieldSynthesizer;

    private final CompleteCatalogGraph graphJoin;

    private final CatalogReaderInterceptor queryRewriter;

    private final ExplicitDataJoin join;

    // DataReadCommandImpl
    private final PrimaryKeyReaders primaryKeyers;
    // DataQueryCommandImpl
    private final QueryReaders queryers;

    private final CatalogDescriptorService catalogService;

    @Inject
    public CatalogReadTransactionImpl(CatalogPluginQueryCommand pluginStorage,
                                      TriggerPluginQueryCommand triggerStorage,
                                      @Named("catalog.storage.trigger") String triggerPluginStorage,
                                      @Named("catalog.storage.metadata") String catalogPluginStorage,
                                      CatalogKeyServices keyDelegate, FieldAccessStrategy access,
                                      EntrySynthesizer synthesizer,
                                      FieldSynthesizer fieldSynthesizer, QueryReaders queryers, PrimaryKeyReaders primaryKeyers,
                                      CompleteCatalogGraph graphJoin,
                                      ExplicitDataJoin join,
                                      CatalogReaderInterceptor queryRewriter, CatalogDescriptorService catalogService) {
        this.keyDelegate = keyDelegate;
        this.access = access;
        this.synthesizer = synthesizer;
        this.fieldSynthesizer = fieldSynthesizer;


        this.graphJoin = graphJoin;
        this.join = join;
        this.queryers = queryers;
        this.primaryKeyers = primaryKeyers;
        this.queryRewriter = queryRewriter;
        this.catalogService = catalogService;
        this.queryers.addCommand(catalogPluginStorage,pluginStorage);
        primaryKeyers.addCommand(catalogPluginStorage,pluginStorage);

        this.queryers.addCommand(triggerPluginStorage,triggerStorage);
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
        CatalogResultCache cache = context.getCache(catalog,context);
        boolean cachedResults = false;

        if (targetEntryId == null) {
            applySorts(filter, catalog.getAppliedSorts());
            applyCriteria(filter, catalog, catalog.getAppliedCriteria(), context, instrospection);

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
            if ( keyCriteria == null) {
                cachedResults = query(filter, catalog, context, instrospection,cache);
            } else {
                List<Object> keys = keyCriteria.getValues();
                if (keys == null) {
                    throw new IllegalArgumentException("malformed criteria");
                }
                if( keyCriteria != null&& filter.getFilters().size()==1 && keys.size()==1){
                    cachedResults = readTargetEntryId(instrospection, cache,catalog, keys.get(0), context);
                }else{
                    if (filter.getCursor() == null) {
                        int ammountOfKeys = keys.size();
                        // only if theres still some unsatisfied idś in criteria
                        if (filter.getStart() < ammountOfKeys) {
                            cachedResults = query(filter, catalog, context, instrospection,cache);
                        }
                    } else {
                        cachedResults = query(filter, catalog, context, instrospection,cache);
                    }
                }
            }
            List<CatalogEntry> result = context.getResults();
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
                    result=null;
                }
            }

            context.setResults(result);

            if(result!=null){
                String[][] joins = filter.getJoins();
                if (joins != null && joins.length > 0) {
                    join.execute(context);
                } else if (!cachedResults&&context.getRequest().getFollowReferences()) {
                    graphJoin.execute(context);
                    cache.put(context,catalog.getDistinguishedName(),result,filter);
                }
            }

        } else {

            cachedResults = readTargetEntryId(instrospection,cache,catalog,targetEntryId,context);

            CatalogEntry originalEntry = context.getResult();

                queryRewriter.interceptResult(originalEntry, context, catalog);
                context.setResults(Collections.singletonList(originalEntry));

                if (originalEntry!=null&&!cachedResults&&context.getRequest().getFollowReferences()) {
                    graphJoin.execute(context);
                    cache.put(context,catalog.getDistinguishedName(),originalEntry);
                }

            log.trace("[RESULT ] {}", originalEntry);
        }

        return CONTINUE_PROCESSING;
    }

    private boolean readTargetEntryId(Instrospection instrospection,CatalogResultCache cache,CatalogDescriptor catalog,Object targetEntryId, CatalogActionContext context) throws Exception {
        CatalogEntry result = cache.get(context, catalog.getDistinguishedName(), targetEntryId);
        boolean cachedResults=false;
        if(result==null){
            FieldDescriptor dn = catalog.getFieldDescriptor(HasDistinguishedName.FIELD);
            if (targetEntryId instanceof String&&dn!=null && dn.isFilterable()) {
                result = cache.get(context, catalog.getDistinguishedName(), targetEntryId);
                if(result==null){
                    result = cache.get(context,catalog.getDistinguishedName(),HasDistinguishedName.FIELD,targetEntryId);
                    if(result==null){
                        result=readVanityId((String) targetEntryId, catalog, context,  instrospection,cache);
                        if(result!=null){
                            cache.put(context,catalog.getDistinguishedName(),access.getPropertyValue(HasDistinguishedName.FIELD,result,null,instrospection),HasDistinguishedName.FIELD,result);
                        }
                    }else{
                        cachedResults=true;
                    }
                }else{
                    cachedResults=true;
                }
            } else {
                result = read(targetEntryId, catalog, context, instrospection);
            }
        }else{
            cachedResults=true;
        }
        context.setResult(result);
        return cachedResults;
    }

    public CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogActionContext context,
                                     Instrospection instrospection, CatalogResultCache cache) throws Exception {
        CatalogEntry regreso = null;
        if (keyDelegate.isPrimaryKey(vanityId)) {
            // almost certainly an Id
            try {
                Object primaryId = keyDelegate.decodePrimaryKeyToken(vanityId);
                if(primaryId!=vanityId || catalog.getFieldDescriptor(catalog.getKeyField()).getDataType()==CatalogEntry.STRING_DATA_TYPE){
                    regreso = read(primaryId, catalog, context, instrospection);
                }

            } catch (NumberFormatException e) {
                log.warn("specified parameter {} could not be used as a primary key", vanityId);
            }

        }

        if (regreso == null) {
            log.debug("token {} returned no results as primery key, attempting to use it as vanity id", vanityId);
            FieldDescriptor dn = catalog.getFieldDescriptor(HasDistinguishedName.FIELD);
            if (dn != null) {
                    if(dn.isFilterable()){
                        FilterData filter = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, vanityId);
                        List<CatalogEntry> results = resolveQueryUnits(filter, catalog, context, instrospection);
                        if (results == null || results.isEmpty()) {
                            log.error("attempt to use {} as discriminator returned no results", vanityId);
                        } else {
                            regreso = results.get(0);
                        }
                    }else{
                        log.error("{} is not a filterable field",HasDistinguishedName.FIELD);
                    }

            }

        }
        return regreso;
    }


    /**
     *
     * @param filterData
     * @param catalog
     * @param context
     * @param instrospection
     * @param cache
     * @return are results cached?
     * @throws Exception
     */
    private boolean query(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
                                   Instrospection instrospection,CatalogResultCache cache) throws Exception {
        List<CatalogEntry> regreso;
        filterData = queryRewriter.interceptQuery(filterData, context, catalog);
        boolean cached = false;
        if (cache == null) {
            regreso = resolveQueryUnits(filterData, catalog, context, instrospection);
        } else {
            regreso = cache.satisfy(context, catalog, filterData);
            if (regreso == null) {
                regreso = resolveQueryUnits(filterData, catalog, context, instrospection);
            }else{
                cached=true;
            }
        }
        context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
                null/* no undo */);

        context.setResults( regreso);
        return cached;
    }

    public CatalogEntry read(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
        CatalogEntry regreso = readUnits(targetEntryId, catalog, context, instrospection);

        context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
                null/* no undo */);
        return regreso;
    }

    private CatalogEntry readUnits(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
        List<String> storageUnits = catalog.getStorage();

        Command storageUnit;
        if (storageUnits == null ||storageUnits.isEmpty() ||storageUnits.size() == 1) {
            if (storageUnits == null || storageUnits.isEmpty()) {
                //default?
                storageUnit = primaryKeyers.getDefault();
            } else {
                storageUnit = primaryKeyers.getCommand(storageUnits.get(0));
            }
            return readUnits(targetEntryId,catalog,context,instrospection,storageUnit);
        } else {
            //first to preset results wins
            CatalogEntry result;
            for (String storageName : storageUnits) {
                storageUnit = primaryKeyers.getCommand(storageName);

                result =  readUnits(targetEntryId,catalog,context,instrospection,storageUnit);
                if (result != null) {
                    return result;
                }
            }
            return null;


        }
    }

    private List<CatalogEntry> resolveQueryUnits(FilterData filter, CatalogDescriptor catalog, CatalogActionContext context, Instrospection instrospection) throws Exception {

        List<String> storageUnits = catalog.getStorage();

        Command storageUnit;
        if (storageUnits == null|| storageUnits.isEmpty() || storageUnits.size() == 1) {
            if (storageUnits == null || storageUnits.isEmpty()) {
                //default?
                storageUnit = queryers.getDefault();
            } else {
                storageUnit = queryers.getCommand(storageUnits.get(0));
            }
            return queryUnit(filter, catalog, context, instrospection, storageUnit);
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
                                    return queryUnit(filter, catalog, context, instrospection, command);
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
                        patials = queryUnit(filter, catalog, context, instrospection, storageUnit);
                        if (patials != null && ! patials.isEmpty()) {
                            return patials;
                        }
                    }
                    return null;

            }


        }

    }

    private List<CatalogEntry> queryUnit(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
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

    private CatalogEntry readUnits(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
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
                    catalogService.getDescriptorForKey(parentCatalogId,context), parentEntityId,
                    context, catalog, instrospection);
        }
        return context.getEntryResult();

    }

    protected void processChildren(List<CatalogEntry> children, CatalogActionContext context, CatalogDescriptor catalog, Instrospection instrospection) throws Exception {
        // we are certain this catalog has a parent, otherwise this DAO would not be called
        Long parentCatalogId = catalog.getParent();
        CatalogDescriptor parent = catalogService.getDescriptorForKey(parentCatalogId,context);
        Object parentEntityId;
        for (CatalogEntry childEntity : children) {
            parentEntityId = synthesizer.getAllegedParentId(childEntity, instrospection,access);
            synthesizer.processChildInheritance(childEntity, parent, parentEntityId, context, catalog,
                    instrospection);
        }
    }

    private void applyCriteria(FilterData filter, CatalogDescriptor catalog, List<? extends FilterCriteria> appliedCriteria, CatalogActionContext context, Instrospection instrospection) throws Exception {
        if (appliedCriteria != null) {
            for (FilterCriteria criteria : appliedCriteria) {
                if (criteria.getEvaluate()) {
                    String operator = criteria.getOperator();
                    ListIterator listIterator = ((List) criteria.getValues()).listIterator();
                    Object criteriaValue = fieldSynthesizer.synthethizeFieldValue(listIterator, context, null, catalog, catalog.getFieldDescriptor(criteria.getPath(0)), instrospection,context.getRuntimeContext().getServiceBus());
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



}
