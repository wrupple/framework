package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.Entity;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.catalogs.domain.CacheInvalidationEvent;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.services.PrimaryKeyEncodingService;

/**
 * 
 * 
 * @author japi
 *
 */
public interface CatalogManager extends Catalog {
	
	 static final String DOMAIN_METADATA = "Namespace"+CatalogDescriptor.CATALOG_ID;
	
	PrimaryKeyEncodingService getKeyEncodingService();

	CatalogActionContext spawn(CatalogActionContext parent);
	
	Context spawn(ExcecutionContext system);

	Command getNew();
	
	Command getRead();
	
	Command getWrite();
	
	Command getDelete();

	CatalogDescriptor getDescriptorForName(String catalog, CatalogActionContext context)throws Exception;


	/**
	 * Catalogs that can be inherited from must be accesible through a numeric key
	 * 
	 * @param key
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws Exception;

	<T extends Entity> List<T> getAvailableCatalogs(CatalogActionContext context) throws Exception;

	CatalogResultCache getCache();
	
	public  void addBroadcastable(CacheInvalidationEvent data, CatalogActionContext ctx);
	public  CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context);

	Object createBatch(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
			Object foreignValue) throws Exception;
}
