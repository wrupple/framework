package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface TableMapper {


    void getTableNameForCatalog(CatalogDescriptor catalog, CatalogActionContext context, StringBuilder builder);

    String getColumnForField(CatalogActionContext context, CatalogDescriptor catalogDescriptor, FieldDescriptor field, boolean qualified);

    String getFieldNameForColumn(String columnName, boolean qualified);


}
