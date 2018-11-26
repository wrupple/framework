package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.FieldDescriptor;

public class CatalogOperand extends CatalogEntryImpl {
    private static final String CATALOG = "CatalogOperand";

    private CatalogReadRequestImpl request; private  FieldDescriptor targetField;

    public CatalogOperand(CatalogReadRequestImpl request, FieldDescriptor targetField) {
        this();
    }

    public CatalogOperand() {

    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    public CatalogReadRequestImpl getRequest() {
        return request;
    }

    public void setRequest(CatalogReadRequestImpl request) {
        this.request = request;
    }

    public FieldDescriptor getTargetField() {
        return targetField;
    }

    public void setTargetField(FieldDescriptor targetField) {
        this.targetField = targetField;
    }
}
