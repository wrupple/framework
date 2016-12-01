package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogChangeEvent;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public interface CatalogActionContext
		extends UserContext, HasParent<CatalogActionContext>, HasResult, CatalogActionRequest {

	final String CATALOG = "CatalogActionContext";

	public ExcecutionContext getExcecutionContext();

	public NamespaceContext getNamespaceContext();

	public SystemCatalogPlugin getCatalogManager();

	CatalogActionContext getRootAncestor();

	TransactionHistory getTransactionHistory();

	public void addBroadcastable(CatalogChangeEvent data);

	public List<CatalogChangeEvent> getEvents();

	public CatalogDescriptor getCatalogDescriptor() throws RuntimeException;
	
	void setCatalogDescriptor(CatalogDescriptor catalog);

	public void addResult(CatalogEntry result);

	public CatalogEntry getOldValue();

	void setOldValue(CatalogEntry old);

	public void setOldValues(List<CatalogEntry> originalEntries);

	public List<CatalogEntry> getOldValues();

	public void addResuls(List<CatalogEntry> result);

	public void setAction(String readAction);

	public void setFilter(FilterData fd);

	/**
	 * @return
	 */
	public <T extends CatalogEntry> List<T> getResults();

	public <T extends CatalogEntry> void setResults(List<T> discriminated);

	void setDomain(Long domain) ;

	void setEntryValue(Object object);

	public <T extends CatalogEntry> T getEntryResult();

	void setNamespace(String domain) throws Exception;


}
