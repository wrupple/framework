package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.FieldDescriptor;

public class CatalogOperand extends OperationImpl  {
    public static final String CATALOG = "CatalogOperand";

    private CatalogActionRequestImpl request; private  FieldDescriptor targetField;

    public CatalogOperand(CatalogActionRequestImpl request, FieldDescriptor targetField) {
        this();
        this.request=request;
        this.targetField=targetField;
    }

    public CatalogOperand() {
        setName(CATALOG);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    public CatalogActionRequestImpl getRequest() {
        return request;
    }

    public void setRequest(CatalogActionRequestImpl request) {
        this.request = request;
    }

    public FieldDescriptor getTargetField() {
        return targetField;
    }

    public void setTargetField(FieldDescriptor targetField) {
        this.targetField = targetField;
    }

    @Override
    public void appendOperand(Object obtainedData) {
        if(this.request==null){
            this.request= (CatalogActionRequestImpl) obtainedData;
        }
    }
}
