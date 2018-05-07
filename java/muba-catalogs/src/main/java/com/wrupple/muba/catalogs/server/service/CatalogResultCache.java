package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;

public interface CatalogResultCache {


	/**
	 * Retrive a single entry from cache
	 * 
	 * @param context
	 * @param catalog
	 * @param targetEntryId
	 * @return
	 */
	<T extends CatalogEntry> T get(CatalogActionContext context, String catalog, Object targetEntryId);
	
	/**
	 * Register an entry with it's implicit key ( using CatalogEntry.getId() in most cases )
	 * 
	 * @param context
	 * @param catalog
	 * @param regreso
	 */
	void put(CatalogActionContext context, String catalog, CatalogEntry regreso);
	
	/**
	 * 
	 * Register an entry with an explicit key 
	 * @param context
	 * @param regreso
	 */
	void put(CatalogActionContext context, String catalogId, Object explicitKey, CatalogEntry regreso);
	
	<T extends CatalogEntry> List<T>  satisfy(CatalogActionContext context, CatalogDescriptor catalog, FilterData filterData);

	void put(CatalogActionContext context, String catalog, List<CatalogEntry> regreso, FilterData filterData);
	
	
	
	void update(CatalogActionContext context, String catalog, CatalogEntry oldValue, CatalogEntry result);

	void delete(CatalogActionContext context, String catalog, Object targetEntryId);
	
	void clearLists(CatalogActionContext context, String catalog);



}
