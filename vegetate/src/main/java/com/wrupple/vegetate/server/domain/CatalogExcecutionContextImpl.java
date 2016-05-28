package com.wrupple.vegetate.server.domain;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.VegetateResultSet;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.SessionContext;

public class CatalogExcecutionContextImpl extends ContextBase implements CatalogExcecutionContext {

	private static final long serialVersionUID = 3599727649189964912L;
	
	/*
	 * SYSTEM CONTEXT
	 */
	
	private final DomainContext domainContext;

	private final RequestScopedContext request;

	private final CatalogExcecutionContext parentValue;

	/*
	 * INPUT
	 */
	private Object entryValue,entry;
	private FilterDataImpl filter;
	private String action, catalog;
	// dont change the name of this variable see RequestTokenizer
	private long totalRequestSize;
	/*
	 * OUTPUT
	 */
	private long totalResponseSize;
	private List<CatalogEntry> results;
	private VegetateResultSet resultSet;
	private Set<ConstraintViolation<?>> constraintViolations;
	private CatalogDescriptor catalogDescriptor;

	// not injectable, always construct with RequestScopedCOntext.spawn
	public CatalogExcecutionContextImpl(DomainContext domainContext, RequestScopedContext requestContext, CatalogExcecutionContext parentValue) {
		this.request = requestContext;
		this.domainContext = domainContext;
		this.parentValue = parentValue;
		totalResponseSize = 0;
		if (parentValue != null) {
			set(parentValue.getDomain(), parentValue.getCatalog(), parentValue.getAction(),  parentValue.getEntry(), parentValue.getEntryValue(),
					parentValue.getFilter());
		}
	}

	public void set(long requestedDomain, String catalog, String action, Object entry, Object entryValue, FilterData filter) {
		setEntry(entry);
		setEntryValue(entryValue);
		setCatalog(catalog);
		setAction(action);
		setFilter(filter);
		getDomainContext().setId(requestedDomain);
	}

	public DomainContext getDomainContext() {
		return domainContext;
	}

	public Long getDomain() {
		return (Long) getDomainContext().getDomain();
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
		this.results=(List)discriminated;
	}


	public CatalogDescriptor getCatalogDescriptor() {
		if (catalogDescriptor == null) {
			if(catalog==null){
				return null;
			}
			setCatalogDescriptor(request.getStorageManager().getCatalogDescriptor(catalog, this));
		}
		return catalogDescriptor;
	}

	public void setCatalogDescriptor(CatalogDescriptor catalog) {
		this.catalogDescriptor = catalog;
	}

	public VegetateResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(VegetateResultSet mainResultSet) {
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
		return getRequest().getFormat();
	}

	public Object getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		//FIXME FILTER DATA KEY ENCODING
		this.entry = getRequest().getStorageManager().getKeyEncodingService().decodePrimaryKeyToken(entry, getCatalogDescriptor());
	}
	
	@Override
	public void setEntry(Object key) {
		if(key instanceof Long){
			this.entry=key;
		}else{
			setEntry((String)key);
		}
	}

	public String getCatalog() {
		if (catalog == null) {
			catalog = catalogDescriptor == null ? null : catalogDescriptor.getCatalogId();
		}
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public SessionContext getSession() {
		return this.request.getSession();
	}

	@Override
	public String toString() {
		return "[catalogEntry=" + entryValue + ", filter=" + filter + ", action=" + action + ", format=" + getFormat() + ", entry=" + entry + ", catalog="
				+ catalog + ", domain=" + getDomain() + "]";
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
		return request.deduceLocale(getDomainContext());
	}

	public void setLocale(String locale) {
		request.setLocale(locale);
	}

	public RequestScopedContext getRequest() {
		return request;
	}

	@Override
	public CatalogExcecutionContext getParent() {
		return parentValue;
	}

	@Override
	public CatalogExcecutionContext getRootAncestor() {
		CatalogExcecutionContext ancestor = getParent();
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
		if(getDomain().longValue()==CatalogEntry.PUBLIC_ID){
			return catalogId;
		}else{
			String domain = getDomain().toString();
			return new StringBuilder(domain.length()+catalogId.length()+1).append(domain).append('_').append(catalogId).toString();
		}
	}

	@Override
	public <T extends CatalogEntry> T getResult() {
		return (T) (results==null? null:results.get(0));
	}


}
