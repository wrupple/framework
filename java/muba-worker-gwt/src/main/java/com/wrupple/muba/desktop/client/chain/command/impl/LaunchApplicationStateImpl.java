package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class LaunchApplicationStateImpl implements LaunchApplicationState {

    protected final PlaceController pc;
    protected final DesktopManager dm;

    private final TransactionalActivityAssembly assembly;
    private final ActivityVegetateEventHandler vegetateHandler;
    /*
        * SERVICES
        */
    private final com.wrupple.muba.event.EventBus eventBus;
    private final ProcessManager pm;
    protected JsArray<JsProcessTaskDescriptor> overridenProcessSteps;


    // cachuky tuku
    @com.google.inject.Inject
    public LaunchApplicationStateImpl(ProcessManager pm, com.wrupple.muba.event.EventBus eventBus, DesktopManager dm, PlaceController pc, TransactionalActivityAssembly assembly,
                                      ActivityVegetateEventHandler vegetateHandler) {
        this.pm = pm;
        this.eventBus = eventBus;
        this.vegetateHandler = vegetateHandler;
        this.assembly = assembly;

    }

    @Inject
    public LaunchApplicationStateImpl(ProcessManager bpm) {
        this.bpm = bpm;
    }


    protected boolean recoverFromMissconfiguredDesktop(DesktopPlace place) {
        DesktopPlace newPlace = new DesktopPlace(DesktopManager.RECOVERY_ACTIVITY);
        newPlace.setFoward(place);
        pc.goTo(newPlace);
        return true;
    }

    @Override
    public void getActivityProcess(final DesktopPlace input, JsApplicationItem actd, DataCallback<ActivityProcess> callback) {
        callback.hook(vegetateHandler);
        eventBus.addHandler(VegetateEvent.TYPE, vegetateHandler);
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
                callback = new ResourceLoadingCallback(dm, callback, scripts, sheets, assembly.getSm(), eventBus);
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

    @Override
    public boolean execute(Context ctx) throws Exception {

        ContainerContext context = (ContainerContext) ctx;


        ApplicationState applicationState = context.getState();


        eventBus.fireEvent(applicationState, context.getRuntimeContext(), null);


        return CONTINUE_PROCESSING;
    }

    public static class SetApplicationStateAndContext extends DataCallback<ActivityProcess> {
        ProcessManager pm;
        AcceptsOneWidget panel;
        EventBus eventBus;
        JsApplicationItem applicationItem;

        public SetApplicationStateAndContext(ProcessManager pm, AcceptsOneWidget panel, EventBus eventBus, JsApplicationItem applicationItem) {
            super();
            this.pm = pm;
            this.panel = panel;
            this.eventBus = eventBus;
            this.applicationItem = applicationItem;
        }

        @Override
        public void execute() {
            pm.setCurrentProcess(applicationItem.getProcessAsId());
            pm.contextSwitch(result, applicationItem, panel, eventBus);
        }

    }


    class ProcessDescriptorCallback extends DataCallback<List<JsProcessDescriptor>> {

        private DataCallback<ActivityProcess> callback;

        public ProcessDescriptorCallback(DataCallback<ActivityProcess> callback) {
            this.callback = callback;
        }

        @Override
        public void execute() {
            if (result == null || result.isEmpty()) {
                // FIXME process 404
                throw new IllegalArgumentException("Activity Descriptor not found for current activity");
            } else {
                JsProcessDescriptor process = result.get(0);
                assembly.start(process, callback, eventBus);

            }
        }

    }
}
