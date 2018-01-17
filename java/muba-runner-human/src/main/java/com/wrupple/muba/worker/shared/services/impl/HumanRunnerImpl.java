package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.Task;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import com.wrupple.muba.worker.shared.services.HumanRunner;
import com.wrupple.muba.worker.shared.services.HumanVariableEligibility;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class HumanRunnerImpl implements HumanRunner {

    private final Provider<HumanVariableEligibility> variableProvider;

    @Inject
    public HumanRunnerImpl(Provider<HumanVariableEligibility> variableProvider) {
        this.variableProvider = variableProvider;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        return field.isWriteable();
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return variableProvider.get().of(field, context);
    }


    @Override
    public boolean solve(ApplicationContext context) {


        //////////////////////////////////////////////////////////////////
        //FIXME  ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡AQUI!!!!!!!!!!!!!!!!!!!!!!!!!
        /////////////////////////////////////////////////////////////////





        return Command.CONTINUE_PROCESSING;
    }


    private void presentJob(ApplicationContext regreso, Task step) {
        String machineCommand;
        Command stateInstance;
        ProblemPresenterChain transactionHandler;
        MachineTask machineInteraction;
        machineCommand = step.getMachineTaskCommandName();
        stateInstance = (ContextualTransactionProcessState) step.getStateInstance();
        if (stateInstance == null) {
            if (machineCommand == null) {
                properties = step.getPropertiesObject();
                GWTUtils.setAttribute(properties, JsProcessTaskDescriptor.TRANSACTION_FIELD, step.getTransactionType());
                transactionHandler = this.transactionHandlerMap.getConfigured(properties, null, null, null);
                transactionHandler.assembleTaskProcessSection(regreso, step);

            } else {
                machineInteraction = machineTaskProvider.get();
                machineInteraction.setTaskDescriptor(step);
                regreso.addState(machineInteraction);
            }
        } else {
            regreso.addState(stateInstance);
        }
    }
}
