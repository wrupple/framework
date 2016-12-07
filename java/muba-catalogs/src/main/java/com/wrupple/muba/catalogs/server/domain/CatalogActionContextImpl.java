package com.wrupple.muba.catalogs.server.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.CatalogChangeEvent;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogActionContextImpl extends ContextBase implements CatalogActionContext {

	private static final long serialVersionUID = 3599727649189964912L;

	private Long id;

	/*
	 * SYSTEM CONTEXT
	 */
	private final SystemCatalogPlugin catalogManager;

	private final NamespaceContext namespace;

	private final ExcecutionContext excecutionContext;

	private final CatalogActionContext parentValue;
	/* usually use the root ancestor of the catalog context */
	private TransactionHistory transaction;

	/*
	 * INPUT
	 */
	private Object entryValue, entry;
	private FilterDataImpl filter;
	@NotNull
	private String action, catalog;
	// dont change the name of this variable see RequestTokenizer
	private long totalRequestSize;
	/*
	 * OUTPUT
	 */
	private List<CatalogEntry> oldValues;
	private List<CatalogChangeEvent> events;

	private List<CatalogEntry> results;
	private CatalogResultSet resultSet;

	private CatalogDescriptor catalogDescriptor;

	// not injectable, always construct with CatalogManager.spawn
	public CatalogActionContextImpl(SystemCatalogPlugin manager, NamespaceContext domainContext,
			ExcecutionContext requestContext, CatalogActionContext parentValue) {
		this.catalogManager = manager;
		this.excecutionContext = requestContext;
		this.namespace = domainContext;
		this.parentValue = parentValue;
		if (parentValue != null) {
			events = new ArrayList<CatalogChangeEvent>(2);
			setEntry(parentValue.getEntry());
			setEntryValue(parentValue.getEntryValue());
			setCatalog((String) parentValue.getCatalog());
			setAction(parentValue.getAction());
			setFilter(parentValue.getFilter());
			setDomain((Long) parentValue.getDomain());
		}
	}

	@Override
	public SystemCatalogPlugin getCatalogManager() {
		return catalogManager;
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return namespace;
	}

	public Long getDomain() {
		return (Long) getNamespaceContext().getId();
	}

	public long getPersonId() throws Exception {
		return (Long) getSession().getStakeHolder();
	}

	public void setEntryValue(Object entry) {
		this.entryValue = entry;
	}

	public Object getEntryValue() {
		return entryValue;
	}

	public CatalogEntry getCatalogEntryAsParsedEntity() {
		return (CatalogEntry) entryValue;
	}

	public FilterDataImpl getFilter() {
		return filter;
	}

	public void setFilter(FilterDataImpl filter) {
		this.filter = filter;
	}

	public void setFilter(FilterData filter) {
		this.filter = (FilterDataImpl) filter;
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
		if (catalogDescriptor == null) {
			setCatalogDescriptor(getCatalogManager().getDescriptorForName(getCatalog(), this));
		}
		return catalogDescriptor;
	}

	public void setCatalogDescriptor(CatalogDescriptor catalog) {
		if (catalog == null) {
			setCatalog(null);
		} else {
			setCatalog(catalog.getDistinguishedName());
		}

		this.catalogDescriptor = catalog;
	}

	public CatalogResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(CatalogResultSet mainResultSet) {
		this.resultSet = mainResultSet;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String getFormat() {
		return getExcecutionContext().getFormat();
	}

	public Object getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = getCatalogManager().decodePrimaryKeyToken(entry);

	}

	@Override
	public void setEntry(Object key) {
		if (key instanceof Long) {
			this.entry = key;
		} else {
			setEntry((String) key);
		}
	}

	@Override
	public String getCatalog() {
		if (catalog == null) {
			catalog = catalogDescriptor == null ? null : catalogDescriptor.getDistinguishedName();
		}
		return catalog;
	}

	public void setCatalog(String catalog) {
		if (this.catalog == null || !this.catalog.equals(catalog)) {
			this.catalog = catalog;
			this.catalogDescriptor = null;
		}

	}

	public SessionContext getSession() {
		return this.excecutionContext.getSession();
	}

	@Override
	public String toString() {
		return "[catalogEntry=" + entryValue + ", filter=" + filter + ", action=" + action + ", format=" + getFormat()
				+ ", entry=" + entry + ", catalog=" + catalog + ", domain=" + getDomain() + "]";
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
		return excecutionContext.deduceLocale(getNamespaceContext());
	}

	public void setLocale(String locale) {
		excecutionContext.setLocale(locale);
	}

	public ExcecutionContext getExcecutionContext() {
		return excecutionContext;
	}

	@Override
	public CatalogActionContext getParent() {
		if (parentValue == this) {
			return null;
		}
		return parentValue;
	}

	@Override
	public CatalogActionContext getRootAncestor() {
		CatalogActionContext ancestor = getParent();
		if (ancestor == null) {
			ancestor = this;
		} else {
			while (ancestor.getParent() != null) {
				ancestor = ancestor.getParent();
			}
		}

		return ancestor;
	}

	@Override
	public <T> T getConvertedResult() {
		return (T) (results == null ? null : results.get(0));
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
	public void setNamespace(String domain) throws Exception {
		if (domain == null) {
			getNamespaceContext().setId(CatalogEntry.PUBLIC_ID, this);
		} else if (CatalogEntry.DOMAIN_TOKEN.equals(domain)) {

			getNamespaceContext().setId(getSession().getDomain(), this);

		} else {
			getNamespaceContext().setId((Long) getCatalogManager().decodePrimaryKeyToken(domain), this);
		}
	}

	@Override
	public void setDomain(Long domain) {
		getNamespaceContext().setId(domain, this);
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {

	}

	@Override
	public String getImage() {
		return null;
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}

	@Override
	public Long getId() {
		return id;
	}


	private void setId(Long decodePrimaryKeyToken) {
		this.id = decodePrimaryKeyToken;
	}

	@Override
	public String getCatalogType() {
		return CATALOG;
	}

	@Override
	public TransactionHistory getTransactionHistory() {
		CatalogActionContext root = getRootAncestor();
		if (root == this) {
			return assertTransaction();
		} else {
			return root.getTransactionHistory();
		}
	}

	public void addBroadcastable(CatalogChangeEvent data) {
		CatalogActionContext ans = getRootAncestor();
		if(ans==this){
			if (data != null) {
				if (data.getEntry() != null) {
					
					if(events==null){
						events = new ArrayList<CatalogChangeEvent>(5);
					}
					events.add(data);
				}
			}
		}else{
			ans.addBroadcastable(data);
		}
		
	}

	public List<CatalogChangeEvent> getEvents() {
		CatalogActionContext ans = getRootAncestor();
		if(ans==this){
			return events;
		}else{
			return ans.getEvents();
		}
	}

	private TransactionHistory assertTransaction() {
		if (transaction == null) {
			transaction = new CatalogUserTransactionImpl(getExcecutionContext().getTransaction());
		}
		return transaction;
	}

	@Override
	public CatalogActionContext spawnChild() {
		return getCatalogManager().spawn(this);
	}

	@Override
	public Object getResult() {
		return getEntryResult();
	}

	@Override
	public void setResult(Object r) {
		setResults((List) Collections.singletonList(r));
	}

}
