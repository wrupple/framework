package com.wrupple.vegetate.domain;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.structure.HasParent;
import com.wrupple.vegetate.server.domain.DomainContext;
import com.wrupple.vegetate.server.services.RequestScopedContext;

public interface CatalogExcecutionContext extends Context, CatalogActionRequest , HasParent<CatalogExcecutionContext>{


	public RequestScopedContext getRequest();
	
	public DomainContext getDomainContext();
	
	CatalogExcecutionContext getRootAncestor();
	
	Long getDomain();

	public String getDomainAwareCatalogId(String catalogId);

	public CatalogDescriptor getCatalogDescriptor();

	public void addResult(CatalogEntry result);

	public void setCatalog(String targetCatalogId);
	public void set(long requestedDomain, String catalog, String action, Object entry, Object entryValue, FilterData filter);

	public void setEntryValue(Object seed);

	public void addResuls(List<CatalogEntry> result);

	public Set<ConstraintViolation<?>> getConstraintViolations();

	public void setTotalResponseSize(long length);

	public void setConstraintViolations(Set<ConstraintViolation<?>> aggregate);

	public void setEntry(Object key);

	public void setAction(String readAction);
	
	public <T  extends CatalogEntry> List<T> getResults();

	public void setFilter(FilterData fd);

	public  <T  extends CatalogEntry>  void setResults(List<T> discriminated);

	public  <T  extends CatalogEntry>  T getResult();
	
}
