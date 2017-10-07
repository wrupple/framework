package com.wrupple.muba.catalogs.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.reserved.HasParent;
import com.wrupple.muba.event.domain.reserved.HasParentValue;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public interface CatalogActionContext
		extends ServiceContext, HasResult<CatalogEntry> {

	final String CATALOG = "CatalogActionContext";

	CatalogActionRequest getRequest();
	
	public NamespaceContext getNamespaceContext();

	public SystemCatalogPlugin getCatalogManager();

	TransactionHistory getTransactionHistory();

	public CatalogDescriptor getCatalogDescriptor() throws RuntimeException;

	public void addResult(CatalogEntry result);

	public CatalogEntry getOldValue();

	void setOldValue(CatalogEntry old);

	public void setOldValues(List<CatalogEntry> originalEntries);

	public List<CatalogEntry> getOldValues();

	public void addResuls(List<CatalogEntry> result);


	/**
	 * @return
	 */
	public <T extends CatalogEntry> List<T> getResults();

	public <T extends CatalogEntry> void setResults(List<T> discriminated);

	public <T extends CatalogEntry> T getEntryResult();

	void setNamespace(String domain) throws Exception;

	public CatalogActionContext spawnChild() throws InvocationTargetException, IllegalAccessException;

    void setCatalogDescriptor(CatalogDescriptor catalog);
}
