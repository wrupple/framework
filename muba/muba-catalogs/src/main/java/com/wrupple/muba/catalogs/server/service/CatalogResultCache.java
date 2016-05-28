package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;

public interface CatalogResultCache {
	
	<T extends CatalogEntry> List<T> satisfy(FilterData filterData);

	<T extends CatalogEntry> T get(Object targetEntryId);
	
	<T extends CatalogEntry> void put(T entry);
	
	<T extends CatalogEntry> void create(T entry);
	
	<T extends CatalogEntry> void delete(T entry);
	
	<T extends CatalogEntry> void update(T old,T newEntry);
	/**
	 * @param entry a list of ids that each will be put into a cache slot
	 * @param filterData stored as a key pointing to a list of ids
	 */
	<T extends CatalogEntry> void put(List<T> entry,FilterData filterData);


	void init(CatalogExcecutionContext context, String catalogId);

	<T extends CatalogEntry> void shiftRemove(T o, FilterData filter);

	<T extends CatalogEntry> void append(T o, FilterData filter);

}
