package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Operation;

public class BinaryOperation extends CatalogEntryImpl implements Operation {
    private static final String CATALOG = "BinaryOperation";

    private Object operand_1;
    private Object operand_2;
    public BinaryOperation(){
    }

    public BinaryOperation(Object operand, String operation) {
        this.operand_1 = operand_1;
        setName(operation);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    public Object getOperand_2() {
        return operand_2;
    }

    public void setOperand_2(Object operand_2) {
        this.operand_2 = operand_2;
    }

    public Object getOperand_1() {
        return operand_1;
    }

    public void setOperand_1(Object operand_1) {
        this.operand_1 = operand_1;
    }

}
