package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.CatalogRunner;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;

public class CatalogRunnerImpl implements CatalogRunner {
    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        Task task = context.getStateValue().getTaskDescriptorValue();

        if(DataContract.READ_ACTION.equals(task.getName())){
            String outputField = task.getOutputField();
            if(outputField!=null && outputField.equals(field.getFieldId())){

                return //FIXME and if we are somehow able to retrive (via grammar, context, etc...) a key ;
            }
        }

        return false;
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return null;
    }

    @Override
    public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) throws Exception {
        return false;
    }
}
