package com.wrupple.muba.catalogs.domain;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;
import com.wrupple.muba.catalogs.server.service.CatalogManager;

public interface CatalogActionContext extends UserContext , HasParent<CatalogActionContext>,CatalogEntry{

	final String CATALOG ="CatalogActionContext";
	CatalogTrigger  getTrigger() ;

	public ExcecutionContext getExcecutionContext();
	
	public NamespaceContext getNamespaceContext();
	
	public CatalogManager getCatalogManager();
	
	CatalogActionContext getRootAncestor();
	
	TransactionHistory getTransactionHistory();
	
	public String getDomainAwareCatalogId(String catalogId);

	public CatalogDescriptor getCatalogDescriptor() throws Exception ;

	public void addResult(CatalogEntry result);

	public void set(long requestedDomain, String catalog, String action, Object entry, Object entryValue, FilterData filter);

	public void setEntryValue(Object seed);
	
	public CatalogEntry getOldValue();
	
	void setOldValue(CatalogEntry old);
	
	public void setOldValues(List<CatalogEntry> originalEntries);
	
	public List<CatalogEntry> getOldValues();

	public void addResuls(List<CatalogEntry> result);

	public Set<ConstraintViolation<?>> getConstraintViolations();

	public void setConstraintViolations(Set<ConstraintViolation<?>> aggregate);

	public void setTotalResponseSize(long length);

	public void setEntry(Object key);

	public void setAction(String readAction);
	
	public <T  extends CatalogEntry> List<T> getResults();

	public  <T  extends CatalogEntry>  T getResult();
	
	public  <T  extends CatalogEntry>  void setResults(List<T> discriminated);

	public void setFilter(FilterData fd);

	public Exception getError();
	
	public void setError(Exception e);

	public Object getEntryValue();

	public String getCatalog();

	public Long getDomain();

	public Object getEntry();

	public FilterData getFilter();

	public String getLocale();

	public void setCatalog(String catalog2);

	public String getAction();

	String getFormat();

	void setDomain(Long domain);

	void setCatalogDescriptor(CatalogDescriptor catalog);

	
	
}
