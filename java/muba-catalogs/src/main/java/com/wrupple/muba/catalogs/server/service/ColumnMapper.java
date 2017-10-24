package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface ColumnMapper {


    String getTableNameForCatalogField(CatalogActionContext context, CatalogDescriptor catalogDescriptor, FieldDescriptor field);

    void buildCatalogFieldQuery(StringBuilder builder, String foreignTableName, CatalogActionContext context,
                                CatalogDescriptor catalogDescriptor, FieldDescriptor field);


}
