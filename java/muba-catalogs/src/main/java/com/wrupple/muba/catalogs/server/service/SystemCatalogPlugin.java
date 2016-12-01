package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.Entity;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.service.CatalogManager;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface SystemCatalogPlugin extends CatalogPlugin, CatalogManager,JSRAnnotationsDictionary,CatalogKeyServices {
	 static final String DOMAIN_METADATA = "Namespace"+CatalogDescriptor.CATALOG_ID;
		
		CatalogActionContext spawn(CatalogActionContext parent);
		
		CatalogActionContext spawn(ExcecutionContext system);
		


		<T extends Entity> List<T> getAvailableCatalogs(CatalogActionContext context) throws Exception;

		CatalogResultCache getCache();
		
		public  CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context);

		/**
		 * @param context
		 * @param catalog
		 * @param field
		 * @param fieldValue
		 * @return id or collection of ids of created objects
		 * @throws Exception
		 */
		Object createBatch(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
				Object fieldValue) throws Exception;

		boolean isJoinableValueField(FieldDescriptor field);

}
