package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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

    private final SystemCatalogPlugin cms;
    private final ObjectMapper mapper;
    private final Provider<CatalogActionRequest> contractProvider;
    private final Provider<NamespaceContext> namespaceProvider;

    @Inject
    public CatalogRequestInterpretImpl(
            SystemCatalogPlugin cms,/* , ObjectMapper mapper */Provider<CatalogActionRequest> contractProvider, Provider<NamespaceContext> namespaceProvider) {
        super();
        this.cms = cms;
        this.contractProvider = contractProvider;
        this.namespaceProvider = namespaceProvider;
        this.mapper = null;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws InvocationTargetException, IllegalAccessException {
        CatalogActionRequest request = (CatalogActionRequest) parent.getServiceContract();
        if (request == null) {
            request = contractProvider.get();
            parent.setServiceContract(request);
        }
        return new CatalogActionContextImpl(cms, namespaceProvider.get(), parent, request);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) ctx;
        CatalogActionRequest request = (CatalogActionRequest) requestContext.getServiceContract();
        CatalogActionContext context = requestContext.getServiceContext();


        // set namespace

        if (request.getDomain()==null || request.getDomain().longValue()==CatalogEntry.CURRENT_NAMESPACE) {
            context.getNamespaceContext().setId((Long) context.getRuntimeContext().getSession().
                    getSessionValue().
                    getDomain(), context);
        }  else {
            context.getNamespaceContext().setId((Long) request.getDomain(), context);
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

			/*
			 * decode incomming primary key
			 */
        Object targetEntryId = request.getEntry();
        if (targetEntryId != null) {
            try {
                targetEntryId = cms.decodePrimaryKeyToken(targetEntryId);
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
                context.getCatalogManager().access().synthesize(catalogDescriptor);
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
                    fieldCriteria.setValues(cms.decodePrimaryKeyFilters(fieldCriteria.getValues()));
                }
            }
        }
        request.setFilter(filter);
        request.setFollowReferences(request.getFollowReferences());
        context.put(CatalogEntry.NAME_FIELD, request.getName());

        return CONTINUE_PROCESSING;
    }


    class CatalogActionContextImpl extends ContextBase implements CatalogActionContext {

        private static final long serialVersionUID = 3599727649189964912L;


	/*
	 * SYSTEM CONTEXT
	 */

        private final SystemCatalogPlugin catalogManager;

        private final NamespaceContext namespace;

        private final RuntimeContext runtimeContext;

        /* usually use the root ancestor of the catalog context */
        private TransactionHistory transaction;

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
        protected CatalogActionContextImpl(SystemCatalogPlugin manager, NamespaceContext domainContext,
                                           RuntimeContext requestContext, CatalogActionRequest catalogActionRequest) {
            this.catalogManager = manager;
            if (requestContext == null) {
                throw new NullPointerException("Must provide an excecution context");
            }
            this.runtimeContext = requestContext;
            this.namespace = domainContext;
            setRequest(catalogActionRequest);
        }

        @Override
        public SystemCatalogPlugin getCatalogManager() {
            return catalogManager;
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
            if (catalogDescriptor == null || !catalogDescriptor.getDistinguishedName().equals(getRequest().getCatalog())) {
                if (getRequest().getCatalog() == null) {
                    throw new NullPointerException("action contract defines no catalog");
                }
                catalogDescriptor = getCatalogManager().getDescriptorForName((String) getRequest().getCatalog(), this);

            }

            return catalogDescriptor;
        }

        @Override
        public String toString() {
            return "CatalogActionContextImpl{" +
                    "request=" + request +
                    '}';
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
            request.setName(DataEvent.READ_ACTION);
            request.setFollowReferences(true);

            getCatalogManager().getRead().execute(this);
            return (T) getResult();
        }


        @Override
        public <T extends CatalogEntry> List<T> delete(String catalog, FilterData o, Object id) throws Exception {
            request.setCatalog(catalog);
            request.setEntry(id);
            request.setFilter(o);
            request.setName(DataEvent.DELETE_ACTION);
            getCatalogManager().getDelete().execute(this);
            return (List<T>) getResults();
        }

        @Override
        public <T extends CatalogEntry> List<T> read(String catalogId, FilterData all) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(null);
            request.setFilter(all);
            request.setName(DataEvent.READ_ACTION);
            getCatalogManager().getRead().execute(this);
            return (List<T>) getResults();
        }


        @Override
        public <T extends CatalogEntry> T write(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(entryId);
            request.setEntryValue(updatedEntry);
            request.setName(DataEvent.WRITE_ACTION);
            request.setFollowReferences(true);

            getCatalogManager().getWrite().execute(this);
            return (T) getResult();
        }

        @Override
        public <T extends CatalogEntry> T create(String catalogId, CatalogEntry createdEntry) throws Exception {
            request.setCatalog(catalogId);
            request.setEntry(null);
            request.setEntryValue(createdEntry);
            request.setName(DataEvent.CREATE_ACTION);
            request.setFollowReferences(true);

            getCatalogManager().getNew().execute(this);
            return (T) getResult();
        }

        @Override
        public <T extends CatalogEntry> T triggerGet(String catalogId, Object entryId) throws Exception {
            return triggerGet(catalogId, entryId, true);
        }


        @Override
        public <T extends CatalogEntry> T triggerGet(String catalogId, Object key, boolean assemble) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntry(key);
            event.setFilter(null);
            event.setFollowReferences(assemble);
            event.setName(DataEvent.READ_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            List<T> results = runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);
            return results == null ? null : results.isEmpty() ? null : results.get(0);
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
            event.setName(DataEvent.READ_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            return runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);

        }


        @Override
        public <T extends CatalogEntry> List<T> triggerDelete(String catalog, FilterData o, Object id) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalog);
            event.setFilter(o);
            event.setEntry(id);
            event.setName(DataEvent.DELETE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            return runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);
        }


        @Override
        public <T extends CatalogEntry> T triggerWrite(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntry(entryId);
            event.setFollowReferences(true);
            event.setEntryValue(updatedEntry);
            event.setName(DataEvent.WRITE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            List<T> results = runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);
            return results == null ? null : results.isEmpty() ? null : results.get(0);
        }


        @Override
        public <T extends CatalogEntry> T triggerCreate(String catalogId, CatalogEntry createdEntry) throws Exception {
            CatalogActionRequestImpl event = new CatalogActionRequestImpl();
            event.setParentValue(request);
            event.setCatalog(catalogId);
            event.setEntryValue(createdEntry);
            event.setName(DataEvent.CREATE_ACTION);
            event.setDomain((Long) getNamespaceContext().getId());
            event.setFollowReferences(true);

            List<T> results = runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);
            return results == null ? null : results.isEmpty() ? null : results.get(0);        }


        @Override
        public <T extends CatalogEntry> T getConvertedResult() {
            return getEntryResult();
        }

        @Override
        public CatalogEntry getResult() {
            return getEntryResult();
        }

        @Override
        public void setResult(CatalogEntry catalogEntry) {
            setResults((List) Collections.singletonList(catalogEntry));
        }

        @Override
        public CatalogActionRequest getRequest() {
            return request;
        }

        public void setRequest(CatalogActionRequest request) {
            this.request = request;
        }
    }

}
