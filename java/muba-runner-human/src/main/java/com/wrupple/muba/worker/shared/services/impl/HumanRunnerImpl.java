package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.desktop.client.chain.ProblemPresenter;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionAssemblerMap;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
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

    private final TransactionAssemblerMap transactionHandlerMap;

    @Inject
    public HumanRunnerImpl(Provider<HumanVariableEligibility> variableProvider, TransactionAssemblerMap transactionHandlerMap) {
        this.variableProvider = variableProvider;
        this.transactionHandlerMap = transactionHandlerMap;
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
    public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) {

        String machineCommand;
        Command stateInstance;
        ProblemPresenter transactionHandler;

        if (overridesTransactionHandler(context)) {

            transactionHandler = getOverridenTransactionHandler(context);
        } else {
            //any runner invocation, batch, a.i, human
            transactionHandler = this.transactionHandlerMap.getConfigured(context);

        }


        //////////////////////////////////////////////////////////////////
        // make this call asynchronous, SynthesizeSolutionEntry becomes SolverEngineCallback//
        //////////////////////////////////////////////////////////////////


        //FIXME missing methods might be in ProblemPresenterImpl

        contextServices.getDesktopManager().updatePlace(currentPlace);


        String catalog = taskDescriptor.getCatalogId();

        JsArrayString filterSelection = null;

        if (saveTo != null) {
            JavaScriptObject savedData = GWTUtils.getAttributeAsJavaScriptObject(context, saveTo);

            if (savedData == null) {
                DesktopPlace place = (DesktopPlace) context.getPlaceController().getWhere();
                String savedRawKeys = place.getProperty(saveTo);
                if (savedRawKeys != null) {
                    filterSelection = split(savedRawKeys);
                    if (filterSelection.length() <= 0) {
                        filterSelection = null;
                    }
                }

            } else {
                context.setUserOutput(savedData);
                callback.setResultAndFinish(context);
                return;
            }
        }

        if (filterSelection == null) {

			/*
             * USER INTERACTION REQUIRED
			 */

            transactionHandler.delegate(context, callback);


        } else {
            /*
			 * PRECIOUS RESULT WILL BE RECOVERED FROM SAVED STATE
			 */

            StorageManager sm = context.getStorageManager();
            DesktopManager dm = context.getDesktopManager();
            ProblemPresenterImpl.AutoSelectionCallback autoCallback = new ProblemPresenterImpl.AutoSelectionCallback(context, callback);
            if (userOutputIsArray) {
                JsFilterData autoSelectionFilter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, filterSelection);

                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, autoSelectionFilter, autoCallback);

            } else {
                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, filterSelection.get(0), autoCallback);
            }
            // support auto create, update, read, etc...

            wrapper.setWidget(new BigFatMessage("..."));
        }


        return Command.CONTINUE_PROCESSING;
    }

    private ProblemPresenter getOverridenTransactionHandler(ApplicationContext context) {
    }

    private boolean overridesTransactionHandler(ApplicationContext context) {
    }


}
