package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.domain.ValidationExpression;
/**
 * 
 * @author japi
 * 
 */
public interface CatalogPlugin extends CatalogTokenInterpret{
	ValidationExpression[] getValidations();
	
	void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogExcecutionContext context) throws Exception;
	
	public void postProcessCatalogDescriptor(CatalogDescriptor c);
	
	CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz, CatalogExcecutionContext context) throws Exception ;



}
