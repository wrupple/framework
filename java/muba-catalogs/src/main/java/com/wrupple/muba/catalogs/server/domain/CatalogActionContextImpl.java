package com.wrupple.muba.catalogs.server.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.KnownException;
import com.wrupple.muba.bootstrap.domain.KnownExceptionImpl;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.service.CatalogManager;

public class CatalogActionContextImpl extends ContextBase implements CatalogActionContext {

	private static final long serialVersionUID = 3599727649189964912L;

	private static final CatalogDescriptor CATALOG_DESCRIPTOR = null;

	private Long id;

	/*
	 * SYSTEM CONTEXT
	 */
	private final CatalogManager catalogManager;

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
	private String action, catalog;
	// dont change the name of this variable see RequestTokenizer
	private long totalRequestSize;
	/*
	 * OUTPUT
	 */
	private List<CatalogEntry> oldValues;
	/**
	 * trigger that gave origin to this context
	 */
	private CatalogTrigger trigger;
	private Exception error;
	private long totalResponseSize;
	private List<CatalogEntry> results;
	private CatalogResultSet resultSet;
	private Set<ConstraintViolation<?>> constraintViolations;
	private CatalogDescriptor catalogDescriptor;

	// not injectable, always construct with RequestScopedCOntext.spawn
	public CatalogActionContextImpl(CatalogManager manager, NamespaceContext domainContext,
			ExcecutionContext requestContext, CatalogActionContext parentValue) {
		this.catalogManager = manager;
		this.excecutionContext = requestContext;
		this.namespace = domainContext;
		this.parentValue = parentValue;
		totalResponseSize = 0;
		if (parentValue != null) {
			set(parentValue.getDomain(), parentValue.getCatalog(), parentValue.getAction(), parentValue.getEntry(),
					parentValue.getEntryValue(), parentValue.getFilter());
		}
	}

	@Override
	public CatalogManager getCatalogManager() {
		return catalogManager;
	}

	public void set(long requestedDomain, String catalog, String action, Object entry, Object entryValue,
			FilterData filter) {
		setEntry(entry);
		setEntryValue(entryValue);
		setCatalog(catalog);
		setAction(action);
		setFilter(filter);
		try {
			getNamespaceContext().setId(requestedDomain, this);
		} catch (Exception e) {
			throw new KnownExceptionImpl("metada unavailable", KnownException.UNREACHABLE, e);
		}
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

	public CatalogDescriptor getCatalogDescriptor() throws Exception {
		if (catalogDescriptor == null) {
			setCatalogDescriptor(getCatalogManager().getDescriptorForName(getCatalog(), this));
		}
		return catalogDescriptor;
	}

	public void setCatalogDescriptor(CatalogDescriptor catalog) {
		if(catalog ==null){
			setCatalog(null);
		}else{
			setCatalog(catalog.getCatalog());
		}
		
		this.catalogDescriptor = catalog;
	}

	public CatalogResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(CatalogResultSet mainResultSet) {
		this.resultSet = mainResultSet;
	}

	public void setConstraintViolations(Set<ConstraintViolation<?>> aggregate) {
		this.constraintViolations = aggregate;
	}

	public Set<ConstraintViolation<?>> getConstraintViolations() {
		return constraintViolations;
	}

	public void setTotalResponseSize(long lenght) {
		this.totalResponseSize = lenght;
	}

	public long getTotalResponseSize() {
		return totalResponseSize;
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
		this.entry = getCatalogManager().getKeyEncodingService().decodePrimaryKeyToken(entry);

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
			catalog = catalogDescriptor == null ? null : catalogDescriptor.getCatalog();
		}
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
		this.catalogDescriptor=null;
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
	public String getDomainAwareCatalogId(String catalogId) {
		if (getDomain().longValue() == CatalogEntry.PUBLIC_ID) {
			return catalogId;
		} else {
			String domain = getDomain().toString();
			return new StringBuilder(domain.length() + catalogId.length() + 1).append(domain).append('_')
					.append(catalogId).toString();
		}
	}

	@Override
	public <T extends CatalogEntry> T getResult() {
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

	public CatalogTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(CatalogTrigger trigger) {
		this.trigger = trigger;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	public void setNamespace(String domain) {
		try {
			if (domain == null) {
				throw new IllegalArgumentException("cannot act upon a null domain");
			} else if (CatalogEntry.DOMAIN_TOKEN.equals(domain)) {

				getNamespaceContext().switchToUserDomain(this);

			} else {
				getNamespaceContext()
						.setId((Long) getCatalogManager().getKeyEncodingService().decodePrimaryKeyToken(domain), this);
			}
		} catch (Exception e) {
			throw new KnownExceptionImpl("unavailable namespace", KnownException.UNREACHABLE, e);
		}
	}

	@Override
	public void setDomain(Long domain) {
		try {
			if (domain == null) {
				throw new IllegalArgumentException("cannot act upon a null domain");
			}
			getNamespaceContext().setId(domain, this);
		} catch (Exception e) {
			throw new KnownExceptionImpl("unavailable namespace", KnownException.UNREACHABLE, e);
		}
	}

	public void setDomain(long domain) {
		try {
			getNamespaceContext().setId(domain, this);
		} catch (Exception e) {
			throw new KnownExceptionImpl("unavailable namespace", KnownException.UNREACHABLE, e);
		}
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
		return getIdAsString();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getIdAsString() {
		return (String) (id == null ? null
				: getCatalogManager().getKeyEncodingService().encodeClientPrimaryKeyFieldValue(getId(),
						CATALOG_DESCRIPTOR.getFieldDescriptor(CatalogEntry.ID_FIELD), CATALOG_DESCRIPTOR));
	}

	@Override
	public void setIdAsString(String id) {
		setId((Long) getCatalogManager().getKeyEncodingService().decodePrimaryKeyToken(id));
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
			transaction = new CatalogUserTransactionImpl(getExcecutionContext().getTransaction());
		}
		return transaction;
	}


	@Override
	public void setCallback(Chain callback) {
		// TODO Auto-generated method stub
		
	}

}
