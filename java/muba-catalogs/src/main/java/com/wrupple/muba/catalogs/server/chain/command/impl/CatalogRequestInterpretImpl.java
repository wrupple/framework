package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.ObjectMapper;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*Set defaults, (like domain language and stuff)*/
@Singleton
public final class CatalogRequestInterpretImpl implements CatalogRequestInterpret {
    protected static final Logger log = LoggerFactory.getLogger(CatalogRequestInterpretImpl.class);

    private final ObjectMapper mapper;
    private final CatalogKeyServices keydelegate;
    private final FieldAccessStrategy access;
    private final CatalogTriggerInterpret triggerInterpret;
    private final Provider<CatalogActionRequest> contractProvider;
    private final Provider<NamespaceContext> namespaceProvider;

    private final Provider<CatalogDescriptor> metadataDescriptorProvider;
    private final Provider<CatalogReadTransaction> readerProvider;
    private final CatalogResultCache cache;
    private final ActionsDictionary dictionary;

    @Inject
    public CatalogRequestInterpretImpl(CatalogKeyServices keydelegate, FieldAccessStrategy access, CatalogTriggerInterpret triggerInterpret, CatalogResultCache cache,/* , ObjectMapper mapper */Provider<CatalogActionRequest> contractProvider, Provider<NamespaceContext> namespaceProvider, @Named(CatalogDescriptor.CATALOG_ID) Provider<CatalogDescriptor> metadataDescriptorProvider, Provider<CatalogReadTransaction> readerProvider, ActionsDictionary dictionary) {
        super();
        this.keydelegate = keydelegate;
        this.access = access;
        this.triggerInterpret = triggerInterpret;
        this.cache=cache;
        this.contractProvider = contractProvider;
        this.namespaceProvider = namespaceProvider;
        this.metadataDescriptorProvider = metadataDescriptorProvider;
        this.readerProvider = readerProvider;
        this.dictionary = dictionary;
        this.mapper = null;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws InvocationTargetException, IllegalAccessException {
        CatalogActionRequest request = (CatalogActionRequest) parent.getServiceContract();
        if (request == null) {
            request = contractProvider.get();
            parent.setServiceContract(request);
        }
        return new CatalogActionContextImpl(dictionary ,cache,namespaceProvider.get(), parent, request);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) ctx;
        CatalogActionRequest request = (CatalogActionRequest) requestContext.getServiceContract();
        CatalogActionContext context = requestContext.getServiceContext();


        // set namespace

        if (request.getDomain()==null || request.getDomain().longValue()==CatalogEntry.CURRENT_NAMESPACE) {
            context.getNamespaceContext().setId(context.getRuntimeContext().getSession().
                    getSessionValue().
                    getDomain(), context);
        }  else {
            context.getNamespaceContext().setId(request.getDomain(), context);
        }

        request.setDomain((Long) context.getNamespaceContext().getId());

			/*
             * READ REQUEST DATA
			 */
        String pressumedCatalogId = (String) request.getCatalog();
        if (CatalogActionRequest.READ_ACTION.equals(pressumedCatalogId)) {
            request.setName(CatalogActionRequest.READ_ACTION);
            pressumedCatalogId = null;
        }

        request.setCatalog(pressumedCatalogId);


        context.setCatalogDescriptor(getCatalogDescriptor(context,pressumedCatalogId));
			/*
			 * decode incomming primary key
			 */
        Object targetEntryId = request.getEntry();
        if (targetEntryId != null) {
            try {
                targetEntryId = keydelegate.decodePrimaryKeyToken(targetEntryId);
                request.setEntry(targetEntryId);
            } catch (NumberFormatException e) {
                log.error("Primary key is not a number", e);
            }
        }

			/*
			 * Parse incomming raw Catalog Entry (if any)
			 */
        Object catalogEntry = request.getEntryValue();
        CatalogDescriptor catalogDescriptor;
        if (catalogEntry != null) {
            if (catalogEntry instanceof CatalogEntry) {
                // do nothing
            } else {
                catalogDescriptor = context.getCatalogDescriptor();
                access.synthesize(catalogDescriptor);
                //request.setEntryValue((CatalogEntry) catalogEntry);
                throw new NotSupportedException("implementar deserialización ya estaa hecha en algun lado");
            }
        }


			/*
			 * Process Incomming filters (if any)
			 */

        FilterData filter = request.getFilter();
        if (filter != null) {
            catalogDescriptor = context.getCatalogDescriptor();
            Collection<? extends FilterCriteria> filterCriterias;
            String filterCriteriaField;
            FieldDescriptor field;
            filterCriterias = filter.getFilters();
            for (FilterCriteria fieldCriteria : filterCriterias) {
                filterCriteriaField = fieldCriteria.getPath(0);
                field = catalogDescriptor.getFieldDescriptor(filterCriteriaField);
                if (field.isKey()) {
                    fieldCriteria.setValues(keydelegate.decodePrimaryKeyFilters(fieldCriteria.getValues()));
                }
            }
        }
        request.setFilter(filter);
        request.setFollowReferences(request.getFollowReferences());
        context.put(CatalogEntry.NAME_FIELD, request.getName());

        return CONTINUE_PROCESSING;
    }


    private CatalogDescriptor getCatalogDescriptor(CatalogActionContext context,String catalogid) throws Exception {

        if(context.isMetadataReady()){
            return context.getCatalogDescriptor();
        }else{

            CatalogDescriptor metadataDescriptor = metadataDescriptorProvider.get();

            CatalogDescriptor result;
            if (CatalogDescriptor.CATALOG_ID.equals(catalogid)) {

                CatalogResultCache metadataCache = context.getCache(metadataDescriptor, context);

                result = metadataCache.get(context, metadataDescriptor.getDistinguishedName(), metadataDescriptor.getId());
                if (result == null) {
                    //no post processing for metadata catalog

                    //FIXME cache with explicit id
                    //metadataCache.put(context,CatalogDescriptor.CATALOG_ID,metadataDescriptor.getDistinguishedName(),metadataDescriptor);
                    metadataCache.put(context, CatalogDescriptor.CATALOG_ID, metadataDescriptor);
                }

                context.getRuntimeContext().getRootAncestor().put(catalogid,metadataCache);

                if(CatalogDescriptor.CATALOG_ID.equals(context.getRequest().getEntry())){
                    context.setResult(metadataDescriptor);
                }

                result = metadataDescriptor;
            }else{

                CatalogActionRequest parentContext = context.getRequest();
                CatalogActionRequest childContext = new CatalogActionRequestImpl();

                childContext.setName(DataContract.READ_ACTION);
                childContext.setEntry(catalogid);
                childContext.setCatalog(CatalogDescriptor.CATALOG_ID);

                context.switchContract(childContext);
                context.setCatalogDescriptor(metadataDescriptor);


                readerProvider.get().execute(context);
                result = context.getConvertedResult();
                log.warn("[full metadata] {}",result);

                context.getRuntimeContext().getRootAncestor().put(catalogid+CatalogActionContext.INCOMPLETO,result);

                if(result==null){
                    throw new CatalogException("No such catalog "+catalogid);
                }
                context.switchContract(parentContext);

            }

            context.setCatalogDescriptor(result);

            return result;
        }
    }



    class CatalogActionContextImpl extends ContextBase implements CatalogActionContext {

        private static final long serialVersionUID = 3599727649189964912L;
	/*
	 * SYSTEM CONTEXT
	 */  private final ActionsDictionary dictionary;

        private final CatalogResultCache cache;

        private final NamespaceContext namespace;

        private final RuntimeContext runtimeContext;

        /*
         * INPUT
         */
        private CatalogActionRequest request;
        // dont change the name of this variable see RequestTokenizer
        private long totalRequestSize;
        /*
         * OUTPUT
         */
        private List<CatalogEntry> oldValues;

        private List<CatalogEntry> results;
        private CatalogResultSet resultSet;

        private CatalogDescriptor catalogDescriptor;

        // not injectable, always construct with an event
        protected CatalogActionContextImpl(ActionsDictionary dictionary,CatalogResultCache cache, NamespaceContext domainContext,
                                           RuntimeContext requestContext, CatalogActionRequest catalogActionRequest) {
            this.cache = cache;
            this.dictionary=dictionary;
            if (requestContext == null) {
                throw new NullPointerException("Must provide an excecution context");
            }
            this.runtimeContext = requestContext;
            this.namespace = domainContext;
            setRequest(catalogActionRequest);
        }


        @Override
        public NamespaceContext getNamespaceContext() {
            if (namespace == null) {
                throw new IllegalStateException("no namespace in context");
            }
            return namespace;
        }

        public Long getDomain() {
            return (Long) getNamespaceContext().getId();
        }

        public long getPersonId() throws Exception {
            return (Long) getSession().getSessionValue().getStakeHolder();
        }


        public void addResult(CatalogEntry entry) {
            if (entry == null) {
                return;
            }
            if (results == null) {
                results = new ArrayList<CatalogEntry>();
            }
            results.add(entry);
        }

        public void addResuls(List<CatalogEntry> entries) {
            if (entries == null || entries.isEmpty()) {
                return;
            }
            if (results == null) {
                results = new ArrayList<CatalogEntry>();
            }
            results.addAll(entries);
        }

        public List<CatalogEntry> getResults() {
            return results;
        }

        @Override
        public <T extends CatalogEntry> void setResults(List<T> discriminated) {
            this.results = (List) discriminated;
        }

        public CatalogDescriptor getCatalogDescriptor() throws CatalogException {
            if(!isMetadataReady()){
                throw new CatalogException("Context is not ready");
            }
            return catalogDescriptor;
        }

        @Override
        public String toString() {
            return "CatalogActionContextImpl{" +
                    "request=" + request +
                    '}';
        }

        public CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context) {
            CatalogResultCache cache = catalog.getOptimization() != 2/* NO_CACHE */ ? this.cache: null;

            return cache;
        }


        public CatalogResultSet getResultSet() {
            return resultSet;
        }

        public void setResultSet(CatalogResultSet mainResultSet) {
            this.resultSet = mainResultSet;
        }


        public SessionContext getSession() {
            return this.runtimeContext.getSession();
        }

        public long getTotalRequestSize() {
            return totalRequestSize;
        }

        public void setTotalRequestSize(long totalRequestSize) {
            this.totalRequestSize = totalRequestSize;
        }

        public void addToRequestSize(long size) {
            totalRequestSize += size;
        }

        public String getLocale() {
            return runtimeContext.deduceLocale(getNamespaceContext());
        }

        public void setLocale(String locale) {
            runtimeContext.setLocale(locale);
        }

        public RuntimeContext getRuntimeContext() {
            return runtimeContext;
        }

        @Override
        public <T extends CatalogEntry> T getEntryResult() {
            return (T) (results == null ? null : results.get(0));
        }

        public CatalogEntry getOldValue() {
            return oldValues == null || oldValues.isEmpty() ? null : oldValues.get(0);
        }

        public void setOldValue(CatalogEntry oldValue) {
            setOldValues(Collections.singletonList(oldValue));
        }

        @Override
        public void setOldValues(List<CatalogEntry> originalEntries) {
            this.oldValues = originalEntries;
        }

        @Override
        public List<CatalogEntry> getOldValues() {
            return oldValues;
        }

        @Override
        public void setCatalogDescriptor(CatalogDescriptor catalog) {
            this.catalogDescriptor = catalog;
            getRequest().setCatalog(catalog.getDistinguishedName());
        }

        @Override
        public <T extends CatalogEntry> T get(String catalogId, Object entryId) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(entryId);
            request.setFilter(null);
            request.setName(DataContract.READ_ACTION);
            request.setFollowReferences(true);

            dictionary.getRead().execute(this);
            return (T) getResult();
        }


        @Override
        public <T extends CatalogEntry> List<T> delete(String catalog, FilterData o, Object id) throws Exception {
            request.setCatalog(catalog);
            request.setEntry(id);
            request.setFilter(o);
            request.setName(DataContract.DELETE_ACTION);
            dictionary.getDelete().execute(this);
            return (List<T>) getResults();
        }

        @Override
        public <T extends CatalogEntry> List<T> read(String catalogId, FilterData all) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(null);
            request.setFilter(all);
            request.setName(DataContract.READ_ACTION);
            dictionary.getRead().execute(this);
            return (List<T>) getResults();
        }


        @Override
        public <T extends CatalogEntry> T write(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(entryId);
            request.setEntryValue(updatedEntry);
            request.setName(DataContract.WRITE_ACTION);
            request.setFollowReferences(true);

            dictionary.getWrite().execute(this);
            return (T) getResult();
        }

        @Override
        public <T extends CatalogEntry> T create(String catalogId, CatalogEntry createdEntry) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(null);
            request.setEntryValue(createdEntry);
            request.setName(DataContract.CREATE_ACTION);
            request.setFollowReferences(true);

            dictionary.getNew().execute(this);
            return (T) getResult();
        }

        @Override
        public <T extends CatalogEntry> T triggerGet(String catalogId, Object entryId) throws Exception {
            return triggerGet(catalogId, entryId, true);
        }

        @Override
        public CatalogDescriptor getDescriptorForKey(Long numericId) throws Exception {
           return triggerGet(CatalogDescriptor.CATALOG_ID,numericId);
        }

        @Override
        public CatalogDescriptor getDescriptorForName(String catalogId) throws Exception {
            CatalogDescriptor foreign = (CatalogDescriptor) getRuntimeContext().getRootAncestor().get(catalogId + CatalogActionContext.INCOMPLETO);
            if(foreign==null){
                log.warn("[incomplete metadata found] NONE");
                foreign=triggerGet(CatalogDescriptor.CATALOG_ID,catalogId);
            }else{
                log.warn("[incomplete metadata found] {}",foreign);

            }
            return foreign;
        }

        @Override
        public List<CatalogEntry> getAvailableCatalogs() throws Exception {
            FilterData filter = FilterDataUtils.newFilterData();
            filter.setConstrained(false);
            return  triggerRead(CatalogDescriptor.CATALOG_ID,filter);
        }

        @Override
        public boolean isMetadataReady() {
            return catalogDescriptor!=null;
        }

        @Override
        public void switchContract(CatalogActionRequest childContext) {
            setRequest(childContext);
        }


        @Override
        public <T extends CatalogEntry> T triggerGet(String catalogId, Object key, boolean assemble) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntry(key);
            event.setFilter(null);
            event.setFollowReferences(assemble);
            event.setName(DataContract.READ_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            return runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
        }




        @Override
        public <T extends CatalogEntry> List<T> triggerRead(String catalogId, FilterData all) throws Exception {
            return triggerRead(catalogId, all, true);
        }

        @Override
        public <T extends CatalogEntry> List<T> triggerRead(String catalogId, FilterData filter, boolean assemble) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setFilter(filter);
            event.setFollowReferences(assemble);
            event.setName(DataContract.READ_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            return runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);

        }

        @Override
        public <T extends CatalogEntry> List<T> triggerDelete(String catalog, FilterData o, Object id) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalog);
            event.setFilter(o);
            event.setEntry(id);
            event.setName(DataContract.DELETE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            return runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
        }


        @Override
        public <T extends CatalogEntry> T triggerWrite(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntry(entryId);
            event.setFollowReferences(true);
            event.setEntryValue(updatedEntry);
            event.setName(DataContract.WRITE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            List<T> results = runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return results == null ? null : results.isEmpty() ? null : results.get(0);
        }


        @Override
        public <T extends CatalogEntry> T triggerCreate(String catalogId, CatalogEntry createdEntry) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntryValue(createdEntry);
            event.setName(DataContract.CREATE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            event.setFollowReferences(true);

            return  runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
        }


        @Override
        public <T > T getConvertedResult() {
            if(request.getEntry()==null){
                return (T) getResults();
            }else{
                return getEntryResult();
            }
        }

        @Override
        public CatalogEntry getResult() {
            return getEntryResult();
        }

        @Override
        public void setResult(CatalogEntry catalogEntry) {
            setResults(Collections.singletonList(catalogEntry));
        }

        @Override
        public CatalogActionRequest getRequest() {
            return request;
        }

        public void setRequest(CatalogActionRequest request) {
            if(request==null){
                throw new NullPointerException("A catalog context requires a contract");
            }
            this.request = request;
        }
    }

}
