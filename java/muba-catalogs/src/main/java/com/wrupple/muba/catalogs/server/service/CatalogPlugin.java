package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContextEvaluationService;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import org.apache.commons.chain.Command;

public interface CatalogPlugin  {

	static final String DOMAIN_METADATA = "Namespace"+CatalogDescriptor.CATALOG_ID;
	//necesaary to explicitly point to context? something.context.old
	static final String SOURCE_OLD = ContextEvaluationService.NAME+".old"+ CatalogEntry.FOREIGN_KEY;
	
	CatalogDescriptor getDescriptorForName(String catalogId,CatalogActionContext context) throws RuntimeException;


	/**
	 * Catalogs that can be inherited from must be accesible through a numeric key
	 * 
	 * @param key
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws RuntimeException;


	//UserCommand getCatalogAction(String action,CatalogActionContext context);
	
	ValidationExpression[] getValidations();

	Command[] getActions();
	
	/*
	 * catalog manager polls catalog plugins for catalog actions and descriptors
	 * depending on context, and exposes a catalog of reserved actions that
	 * plugins may not overwrite
	 */
	void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) throws Exception;
	
	public void postProcessCatalogDescriptor(CatalogDescriptor c);
}
