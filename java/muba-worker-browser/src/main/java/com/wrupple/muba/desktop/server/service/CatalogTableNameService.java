package com.wrupple.muba.desktop.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public interface
CatalogTableNameService {

    String getTableNameForCatalog(CatalogDescriptor catalog, Long domain);

    String getTableNameForCatalogFiled(CatalogDescriptor catalog, String field, Long domain);

    String getTableNameForCatalogFiled(CatalogDescriptor catalogDescriptor, FieldDescriptor field, Long domain);

}
