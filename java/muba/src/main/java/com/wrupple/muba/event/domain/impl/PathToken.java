package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.reserved.HasResults;

import java.util.List;

public class PathToken implements HasResults<CatalogEntry> {

    private final ContractDescriptor foreignCatalog;
    private final FieldDescriptor targetField;
    private List<CatalogEntry> results;

    public PathToken(ContractDescriptor foreignCatalog, FieldDescriptor targetField) {
        this.targetField=targetField;
        this.foreignCatalog=foreignCatalog;
    }

    public ContractDescriptor getForeignCatalog() {
        return foreignCatalog;
    }

    public FieldDescriptor getTargetField() {
        return targetField;
    }


    public List<CatalogEntry> getResults() {
        return results;
    }

    @Override
    public <T extends CatalogEntry> void setResults(List<T> discriminated) {
        this.results = (List<CatalogEntry>) discriminated;
    }
}
