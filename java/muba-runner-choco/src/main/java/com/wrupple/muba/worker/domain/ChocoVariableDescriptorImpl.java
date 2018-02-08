package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.VariableDescriptor;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.impl.RealVarImpl;

/**
 * Created by rarl on 17/05/17.
 */
public class ChocoVariableDescriptorImpl implements VariableDescriptor {
    private final Variable variable;
    private final FieldDescriptor field;
    private final Long runner;

    public ChocoVariableDescriptorImpl(Variable variable, FieldDescriptor field, Long runner) {
        this.variable = variable;
        this.field = field;
        this.runner = runner;
    }

    public Variable getVariable() {
        return variable;
    }

    public FieldDescriptor getField() {
        return field;
    }

    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
    }

    @Override
    public Object getResult() {
        int type = field.getDataType();
        switch (type){
            case CatalogEntry.INTEGER_DATA_TYPE: return ((IntVar)variable).getValue();
            case CatalogEntry.BOOLEAN_DATA_TYPE: return ((BoolVar)variable).getValue()==1;
            //FIXME IS THIS THE SOLUTION?
            case CatalogEntry.NUMERIC_DATA_TYPE: return  ((RealVarImpl)variable).getLB();
        }
        throw new IllegalStateException("Solution variables may only be of integer type");
    }

    @Override
    public void setResult(Object o) {

    }

    @Override
    public Long getRunner() {
        return runner;
    }
}
