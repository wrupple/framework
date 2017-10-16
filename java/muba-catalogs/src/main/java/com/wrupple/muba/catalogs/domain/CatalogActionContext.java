package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.domain.reserved.HasResults;

public interface CatalogActionContext
		extends ServiceContext, HasResult<CatalogEntry>,HasResults<CatalogEntry> {

	final String CATALOG = "CatalogActionContext";
	String INCOMPLETO ="_incompleto" ;

	CatalogActionRequest getRequest();
	
	public NamespaceContext getNamespaceContext();

	public SystemCatalogPlugin getCatalogManager();

	public CatalogDescriptor getCatalogDescriptor() throws RuntimeException;

	public void addResult(CatalogEntry result);

	public CatalogEntry getOldValue();

	void setOldValue(CatalogEntry old);

	public void setOldValues(List<CatalogEntry> originalEntries);

	public List<CatalogEntry> getOldValues();

	public void addResuls(List<CatalogEntry> result);

	public <T extends CatalogEntry> T getEntryResult();

    void setCatalogDescriptor(CatalogDescriptor catalog);

	<T extends CatalogEntry> T get(String catalogId, Object entryId) throws Exception;

	<T extends CatalogEntry> T triggerGet(String catalogId, Object entryId) throws Exception;

	<T extends CatalogEntry> List<T> read(String catalogId,FilterData all) throws Exception;

	<T extends CatalogEntry> List<T> triggerRead(String catalogId,FilterData all) throws Exception;

	<T extends CatalogEntry> List<T> delete(String catalog, FilterData o, Object id) throws Exception;

	<T extends CatalogEntry> List<T> triggerDelete(String catalog, FilterData o, Object id) throws Exception;



    <T extends CatalogEntry> T  write(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception;

	<T extends CatalogEntry> T  triggerWrite(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception;

	<T extends CatalogEntry> T  create(String catalogId,  CatalogEntry createdEntry) throws Exception;

	<T extends CatalogEntry> T  triggerCreate(String catalogId,  CatalogEntry createdEntry) throws Exception;


	public <T extends CatalogEntry> List<T>  triggerRead(String catalogId, FilterData filter, boolean assemble) throws Exception ;

	CatalogDescriptor getDescriptorForKey(Long numericId) throws Exception;

	CatalogDescriptor getDescriptorForName(String catalogId) throws Exception;


	<T extends CatalogEntry> T triggerGet(String catalogId, Object key, boolean assemble) throws Exception;

    List<CatalogIdentification> getAvailableCatalogs() throws Exception;

	boolean isMetadataReady();

	void switchContract(CatalogActionRequest childContext);
}
