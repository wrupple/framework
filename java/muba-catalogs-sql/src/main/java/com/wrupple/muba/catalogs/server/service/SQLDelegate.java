package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.collections.KeyValue;

import java.util.List;

public interface SQLDelegate {

    /**
     * @param filterStringBuffer
     * @param criteriaSize
     * @param catalogDescriptor
     * @param context
     * @return number of criteri written
     */
    int buildQueryHeader(TableMapper tableNames, StringBuilder filterStringBuffer, int criteriaSize,
                         CatalogDescriptor catalogDescriptor, CatalogActionContext context, List<KeyValue> partitions);
}
