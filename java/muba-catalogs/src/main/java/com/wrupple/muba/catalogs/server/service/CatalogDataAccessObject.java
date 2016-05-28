package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.services.Transactional;

public interface CatalogDataAccessObject<T extends CatalogKey> extends Transactional {
	
	List<T> read(FilterData filterData) throws Exception;

	T read(Object targetEntryId) throws Exception;

	T update(T originalEntry, T updatedEntry) throws Exception;

	T create(T o) throws Exception;

	T delete(T o) throws Exception;
	
	void setContext(CatalogExcecutionContext context);

	CatalogExcecutionContext getContext();
}
