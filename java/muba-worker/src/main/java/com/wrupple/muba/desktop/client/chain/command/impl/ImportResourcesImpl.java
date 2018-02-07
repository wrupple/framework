package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ImportResources;
import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ImportResourcesImpl implements ImportResources {


    private final ImportResourcesCallback importResourcesCallback;

    @Inject
    public ImportResourcesImpl( ImportResourcesCallback importResourcesCallback) {
        this.importResourcesCallback = importResourcesCallback;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ContextSwitchRuntimeContext context = (ContextSwitchRuntimeContext) ctx;
         /*
          *  Load transaction data
		  */
        final Application applicationItem = (Application) context.getContextSwitch().getState().getHandleValue();

        List<CatalogActionRequest> actions = transform(applicationItem.getDependenciesValues());

        context.getStorageManager().perform(context, actions);

        return CONTINUE_PROCESSING;
    }



    @Override
    public void getActivityProcess(final DesktopPlace input, JsApplicationItem actd, DataCallback<ActivityProcess> callback) {

        /*
         * Load transaction data
         */
        final JsApplicationItem applicationItem;
        if (actd == null) {
            applicationItem = null;
        } else {
            applicationItem = actd.cast();

            JsArrayString scripts = applicationItem.getRequiredScriptsArray();
            JsArrayString sheets = applicationItem.getRequiredStyleSheetsArray();

            if ((scripts != null && scripts.length() > 0) || (sheets != null && sheets.length() > 0)) {
                callback = new ImportResourcesCallbackImpl.ResourceLoadingCallback(dm, callback, scripts, sheets, assembly.getSm(), eventBus);
            }

            final String welcomeProcessId = applicationItem.getWelcomeProcess();
            if (welcomeProcessId != null) {

                callback.hook(new DataCallback<ActivityProcess>() {
                    @Override
                    public void execute() {
                        final StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback = new DataCallback<Process<JavaScriptObject, JavaScriptObject>>() {

                            @Override
                            public void execute() {
                                JsTransactionApplicationContext i = JsTransactionApplicationContext.createObject().cast();
                                StateTransition<JavaScriptObject> o = DataCallback.nullCallback();
                                pm.processSwitch(result, applicationItem.getName(), i, o, result.getContext());

                            }
                        };
                        assembly.loadAndAssembleProcess(welcomeProcessId, transactionInfoCallback);
                    }
                });
            }
        }
        assembly.setApplicationItem(applicationItem);

        if (overridenProcessSteps == null) {
            StateTransition transactionInfoCallback = new ProcessDescriptorCallback(callback);
            String processId = applicationItem.getProcessAsId();
            assembly.loadProcess(processId, transactionInfoCallback);
        } else {
            assembly.assembleActivityProcess(overridenProcessSteps, callback);
        }

    }
}
