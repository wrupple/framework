package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.NotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*Set defaults, (like domain language and stuff)*/
@Singleton
public final class CatalogRequestInterpretImpl implements CatalogRequestInterpret {
    protected static final Logger log = LogManager.getLogger(CatalogRequestInterpretImpl.class);


    private final CatalogKeyServices keydelegate;
    private final CatalogDescriptorService catalogService;
    private final FieldAccessStrategy access;
    private final Provider<CatalogActionRequest> contractProvider;
    private final Provider<NamespaceContext> namespaceProvider;


    private final CatalogResultCache cache;
    private final ActionsDictionary dictionary;

    @Inject
    public CatalogRequestInterpretImpl(CatalogKeyServices keydelegate, CatalogDescriptorService catalogService, FieldAccessStrategy access, CatalogResultCache cache,/* , ObjectMapper mapper */Provider<CatalogActionRequest> contractProvider, Provider<NamespaceContext> namespaceProvider, ActionsDictionary dictionary) {
        super();
        this.keydelegate = keydelegate;
        this.catalogService = catalogService;
        this.access = access;
        this.cache=cache;
        this.contractProvider = contractProvider;
        this.namespaceProvider = namespaceProvider;
        this.dictionary = dictionary;
    }



    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {

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

        if(!context.isMetadataReady()){
            context.setCatalogDescriptor(catalogService.getDescriptorForName(pressumedCatalogId,context));
        }
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
                throw new IllegalArgumentException("action value is not a catalogEntry :"+catalogEntry);
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


    @Override
    public Provider<CatalogActionContext> getProvider(RuntimeContext runtime) {
        return new Provider<CatalogActionContext>() {
            @Override
            public CatalogActionContext get() {
                return new CatalogActionContextImpl(contractProvider,dictionary ,cache,namespaceProvider.get());
            }
        };
    }


    static class CatalogActionContextImpl extends ContextBase implements CatalogActionContext {

        private static final long serialVersionUID = 3599727649189964912L;
	/*
	 * SYSTEM CONTEXT
	 */  private final ActionsDictionary dictionary;

        private final CatalogResultCache cache;

        private final NamespaceContext namespace;
        private final Provider<CatalogActionRequest> contractProvider;

        private RuntimeContext runtimeContext;

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

        private List<CatalogColumnResultSet> resultSet;

        // not injectable, always construct with an event
        protected CatalogActionContextImpl(Provider<CatalogActionRequest> contractProvider, ActionsDictionary dictionary, CatalogResultCache cache, NamespaceContext domainContext) {
            this.cache = cache;
            this.contractProvider=contractProvider;
            this.dictionary=dictionary;
            this.namespace = domainContext;
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
            List<CatalogEntry> results=request.getResults();
            if ( results== null) {
                results = new ArrayList<CatalogEntry>();
                request.setResults(results);
            }
            results.add(entry);
        }

        public void addResuls(List<CatalogEntry> entries) {
            if (entries == null || entries.isEmpty()) {
                return;
            }
            List<CatalogEntry> results=request.getResults();
            if (results == null) {
                results = new ArrayList<CatalogEntry>();
                request.setResults(results);
            }
            results.addAll(entries);
        }


        public List<CatalogEntry> getResults() {
            return request.getResults();
        }

        @Override
        public <T extends CatalogEntry> void setResults(List<T> discriminated) {
            request.setResults((List) discriminated);
        }

        public CatalogDescriptor getCatalogDescriptor() throws CatalogException {
            if(!isMetadataReady()){
                throw new CatalogException("Context is not ready");
            }
            return getRequest().getCatalogValue();
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


        public List<CatalogColumnResultSet> getResultSet() {
            return resultSet;
        }

        @Override
        public void setResultSet(ArrayList<CatalogColumnResultSet> regreso) {

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
        public void setRuntimeContext(RuntimeContext requestContext) {
            if (requestContext == null) {
                throw new NullPointerException("Must provide an excecution context");
            }
            this.runtimeContext = requestContext;
            if (getRequest() == null) {
                setRequest((CatalogActionRequest) requestContext.getServiceContract());
                if (getRequest() == null) {
                    setRequest(contractProvider.get());
                    requestContext.setServiceContract(request);
                }
            }

        }

        @Override
        public <T extends CatalogEntry> T getEntryResult() {
            return (T) (request.getResults() == null ? null : request.getResults().get(0));
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
            getRequest().setCatalogValue(catalog);
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
        public List<CatalogEntry> getAvailableCatalogs() throws Exception {
            FilterData filter = FilterDataUtils.newFilterData();
            filter.setConstrained(false);
            return  triggerRead(CatalogDescriptor.CATALOG_ID,filter);
        }

        @Override
        public boolean isMetadataReady() {
            return getRequest().getCatalogValue()!=null;
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
            runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return (T) event.getResults().get(0);
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
            runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return (List)event.getResults();

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
            runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return (List)event.getResults();
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
            runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return (T) event.getResults().get(0);
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
            runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
            return (T) event.getResults().get(0);
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
            if(catalogEntry==null){
                setResults(null);
            }else{
                setResults(Collections.singletonList(catalogEntry));
            }
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
