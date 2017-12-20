package com.wrupple.muba.desktop.server.service.impl;

import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Allows for each domain to have a table namespace and a global "null"-domain
 * namespace
 * 
 * @author japi
 *
 */
public class CatalogTableNameServiceImpl implements CatalogTableNameService {

	private final boolean multitenant;

	@Inject
	public CatalogTableNameServiceImpl(@Named("system.multitenant") Boolean multitenant) {
		super();
		this.multitenant = multitenant;
	}

	@Override
	public String getTableNameForCatalog(CatalogDescriptor catalogCatalog, Long domain) {
		if (multitenant && domain != null) {
			return catalogCatalog.getCatalogId() + "_" + domain;
		} else {
			return catalogCatalog.getCatalogId();
		}
	}

	@Override
	public String getTableNameForCatalogFiled(CatalogDescriptor catalog, String field, Long domain) {
		return getTableNameForCatalog(catalog, domain) + "_" + field;
	}

	@Override
	public String getTableNameForCatalogFiled(CatalogDescriptor catalogDescriptor, FieldDescriptor field, Long domain) {
		return getTableNameForCatalog(catalogDescriptor, domain) + "_" + field.getFieldId();
	}

}
