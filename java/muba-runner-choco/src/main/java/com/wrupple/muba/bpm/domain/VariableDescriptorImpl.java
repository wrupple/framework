package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.impl.RealVarImpl;

/**
 * Created by rarl on 17/05/17.
 */
public class VariableDescriptorImpl implements VariableDescriptor{
    private final Variable variable;
    private final FieldDescriptor field;

    public VariableDescriptorImpl(Variable variable, FieldDescriptor field) {
        this.variable = variable;
        this.field = field;
    }

    public Variable getVariable() {
        return variable;
    }

    public FieldDescriptor getField() {
        return field;
    }

    @Override
    public Object getValue() {
        int type = field.getDataType();
        switch (type){
            case CatalogEntry.INTEGER_DATA_TYPE: return ((IntVar)variable).getValue();
            case CatalogEntry.BOOLEAN_DATA_TYPE: return ((BoolVar)variable).getValue()==1;
            //FIXME IS THIS THE SOLUTION?
            case CatalogEntry.NUMERIC_DATA_TYPE: return  ((RealVarImpl)variable).getLB();
        }
        throw new IllegalStateException("Solution variables may only be of integer type");
    }
}
