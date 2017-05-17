package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

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
        }
        throw new IllegalStateException("Solution variables may only be of integer type");
    }
}
