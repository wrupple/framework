package com.wrupple.muba.catalogs.server.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
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

	private final RuntimeContext runtimeContext;

	private final CatalogActionContext parentValue;
	/* usually use the root ancestor of the catalog context */
	private TransactionHistory transaction;

	public boolean getFollowReferences() {
		return followReferences;
	}

	public void setFollowReferences(boolean followReferences) {
		this.followReferences = followReferences;
	}

	private boolean followReferences;

	/*
	 * INPUT
	 */
	private Object entryValue, entry;
	private FilterDataImpl filter;
	@NotNull
	private String name, catalog;
	// dont change the name of this variable see RequestTokenizer
	private long totalRequestSize;
	/*
	 * OUTPUT
	 */
	private List<CatalogEntry> oldValues;

	private List<CatalogEntry> results;
	private CatalogResultSet resultSet;

	private CatalogDescriptor catalogDescriptor;

	// not injectable, always construct with CatalogManager.spawn
	public CatalogActionContextImpl(SystemCatalogPlugin manager, NamespaceContext domainContext,
                                    RuntimeContext requestContext, CatalogActionContext parentValue) {
		this.catalogManager = manager;
		if(requestContext==null){
			throw new NullPointerException("Must provide an excecution context");
		}
		this.runtimeContext = requestContext;
		this.namespace = domainContext;
		this.parentValue = parentValue;
		if (parentValue != null) {
			setEntry(parentValue.getEntry());
			setEntryValue(parentValue.getEntryValue());
			setCatalog((String) parentValue.getCatalog());
			setAction(parentValue.getName());
			setFilter(parentValue.getFilter());
            setFollowReferences(parentValue.getFollowReferences());
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
		return getName();
	}

	public void setAction(String action) {
		setName(action);
	}

	@Override
	public String getFormat() {
		return getRuntimeContext().getFormat();
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
		return this.runtimeContext.getSession();
	}

	@Override
	public String toString() {
		return "[catalogEntry=" + entryValue + ", filter=" + filter + ", action=" + name + ", format=" + getFormat()
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
		return runtimeContext.deduceLocale(getNamespaceContext());
	}

	public void setLocale(String locale) {
		runtimeContext.setLocale(locale);
	}

	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
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
this.name=name;
	}

	@Override
	public String getName() {
		return name;
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


	private TransactionHistory assertTransaction() {
		if (transaction == null) {
			transaction = new CatalogUserTransactionImpl(getRuntimeContext().getEventBus().getTransaction());
		}
		return transaction;
	}

	@Override
	public CatalogActionContext spawnChild() {
		return getCatalogManager().spawn(this);
	}


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
}
