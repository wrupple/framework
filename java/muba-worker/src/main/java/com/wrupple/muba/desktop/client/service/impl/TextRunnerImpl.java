package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.HumanRunner;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.apache.commons.chain.Command;

public class TextRunnerImpl implements HumanRunner {
    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        return field.isWriteable();
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return null;
    }

    @Override
    public boolean solve(ApplicationContext context) {


        return Command.CONTINUE_PROCESSING;
    }
}
