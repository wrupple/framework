package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Operation;

public class BinaryOperation extends OperationImpl {
    private static final String CATALOG = "BinaryOperation";
    private FieldDescriptor targetField;
    private Object operand_1;
    private Object operand_2;
    private Object operandVariable_2;
    private Object operandVariable_1;
    private PathToken path;

    public BinaryOperation(Object operand, FieldDescriptor targetField, String operation) {
        this.operand_1 = operand;
        this.targetField=targetField;
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

    public Object getOperandVariable_2() {
        return operandVariable_2;
    }

    public void setOperandVariable_2(Object operandVariable_2) {
        this.operandVariable_2 = operandVariable_2;
    }

    public Object getOperandVariable_1() {
        return operandVariable_1;
    }

    public void setOperandVariable_1(Object operandVariable_1) {
        this.operandVariable_1 = operandVariable_1;
    }

    public FieldDescriptor getTargetField() {
        return targetField;
    }

    public void setTargetField(FieldDescriptor targetField) {
        this.targetField = targetField;
    }

    @Override
    public void appendOperand(Object obtainedData) {
        if(getOperand_1()==null){
            setOperand_1(obtainedData);
        }else if(getOperand_2()==null){
            setOperand_2(obtainedData);
        }else{
            throw new IllegalStateException("Unable to append a new operand to a binary operation with both operands present");
        }
    }

    @Override
    public PathToken getPath() {
        return path;
    }

    public void setPath(PathToken path) {
        this.path = path;
    }
}
