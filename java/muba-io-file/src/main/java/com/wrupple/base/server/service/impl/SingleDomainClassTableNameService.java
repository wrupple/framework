package com.wrupple.base.server.service.impl;

import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 * Ignores domain and sets the table name to the clazz's simple name
 * 
 * @author japi
 *
 */
public class SingleDomainClassTableNameService implements CatalogTableNameService {

	public SingleDomainClassTableNameService() {
	}

	@Override
	public String getTableNameForCatalog(CatalogDescriptor catalogCatalog, Long domain) {
		return catalogCatalog.getClazz().replace('.', '_');
	}


	@Override
	public String getTableNameForCatalogFiled(CatalogDescriptor catalog, String field, Long domain) {
		return getTableNameForCatalog(catalog, domain)+"_"+field;
	}

	@Override
	public String getTableNameForCatalogFiled(CatalogDescriptor catalogDescriptor, FieldDescriptor field, Long domain) {
		return getTableNameForCatalogFiled(catalogDescriptor, field.getFieldId(), domain);
	}

}
