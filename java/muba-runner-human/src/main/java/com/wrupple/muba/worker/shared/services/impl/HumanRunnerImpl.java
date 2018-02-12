package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.PresentProblem;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.services.HumanRunner;
import com.wrupple.muba.worker.shared.services.HumanVariableEligibility;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class HumanRunnerImpl implements HumanRunner {

    private final Provider<HumanVariableEligibility> variableProvider;
    private final PresentProblem presentProblemChain;


    @Inject
    public HumanRunnerImpl(Provider<HumanVariableEligibility> variableProvider, PresentProblem chain) {
        this.variableProvider = variableProvider;
        presentProblemChain = chain;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        return field.isWriteable();
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return variableProvider.get().of(field, (HumanApplicationContext)context);
    }

    @Override
    public boolean solve(ApplicationContext c, StateTransition<ApplicationContext> callback) throws Exception {
        HumanApplicationContext context = (HumanApplicationContext) c;
        context.setCallback(callback);
            /*
             * FIXME PRECIOUS RESULT SHOULD BE RECOVERED FROM userSelection, and presented as proposed solution(alternative runner?)
             *

            StorageManager sm = context.getStorageManager();
            DesktopManager dm = context.getDesktopManager();
            ProblemPresenterImpl.AutoSelectionCallback autoCallback = new ProblemPresenterImpl.AutoSelectionCallback(context, callback);
            if (userOutputIsArray) {
                JsFilterData autoSelectionFilter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, userSelection);

                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, autoSelectionFilter, autoCallback);

            } else {
                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, userSelection.get(0), autoCallback);
            }
            // support auto create, update, read, etc...

            wrapper.setWidget(new BigFatMessage("..."));
            */

        presentProblemChain.execute(context);

        return Command.CONTINUE_PROCESSING;
    }





}
