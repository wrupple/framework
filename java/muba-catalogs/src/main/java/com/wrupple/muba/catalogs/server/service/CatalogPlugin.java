package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;

public interface CatalogPlugin  {
	
	CatalogDescriptor getDescriptorForName(String catalogId,CatalogActionContext context) throws Exception;


	/**
	 * Catalogs that can be inherited from must be accesible through a numeric key
	 * 
	 * @param key
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws Exception;

	

	//UserCommand getCatalogAction(String action,CatalogActionContext context);
	
	ValidationExpression[] getValidations();
	
	/*
	 * catalog manager polls catalog plugins for catalog actions and descriptors
	 * depending on context, and exposes a catalog of reserved actions that
	 * plugins may not overwrite
	 */
	void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) throws Exception;
	
	public void postProcessCatalogDescriptor(CatalogDescriptor c);
}
