package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.impl.AbstractVariableDescriptor;
import com.wrupple.muba.event.domain.impl.CatalogOperand;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.List;

public class AssignedForeignKey extends AbstractVariableDescriptor {

    private final Long runner;
    private final FieldDescriptor field;
    private final Variable variable;
    private final List<CatalogEntry> results;

    public AssignedForeignKey(Long runner, FieldDescriptor field, Variable variable, List<CatalogEntry> results) {
        this.runner = runner;
        this.field = field;
        this.variable = variable;
        this.results = results;
    }

    @Override
    public FieldDescriptor getField() {
        return field;
    }

    @Override
    public boolean isSolved() {
        return variable.isInstantiated();
    }

    @Override
    public Object getResult() {
        return results.get(((IntVar)variable).getValue()).getId();
    }

    @Override
    public Long getRunner() {
        return runner;
    }
}
