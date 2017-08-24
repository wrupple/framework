package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.reserved.HasParent;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public interface CatalogActionContext
		extends ServiceContext, HasParent<CatalogActionContext>, HasResult<CatalogEntry>, CatalogActionRequest {

	final String CATALOG = "CatalogActionContext";
	
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
