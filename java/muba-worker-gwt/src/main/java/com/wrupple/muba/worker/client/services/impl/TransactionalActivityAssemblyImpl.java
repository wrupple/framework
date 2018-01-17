package com.wrupple.muba.worker.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionAssemblerMap;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.client.services.command.GoToCommand;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.worker.client.activity.ActivityProcess;
import com.wrupple.muba.worker.client.activity.process.impl.ActivityProcessImpl;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.InitializeActivityContext;
import com.wrupple.muba.worker.client.activity.process.state.MachineTask;
import com.wrupple.muba.worker.client.activity.process.state.ReadNextPlace;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;

import javax.inject.Provider;
import java.util.List;

public class TransactionalActivityAssemblyImpl implements TransactionalActivityAssembly {

    /**
     * Transaction type service
     */
    private final TransactionAssemblerMap transactionHandlerMap;
    private final StorageManager sm;
    /**
     * outputHandlerService
     */
    private Provider<ReadNextPlace> exitProvider;

    private Provider<InitializeActivityContext> enterProvider;

    private Provider<MachineTask> machineTaskProvider;
    private JsApplicationItem applicationItem;
    private DesktopManager dm;

    @Inject
    public TransactionalActivityAssemblyImpl(DesktopManager dm, Provider<MachineTask> machineTaskProvider, StorageManager sm, Provider<InitializeActivityContext> enter,
                                             Provider<ReadNextPlace> exit, TransactionAssemblerMap transactionHandlerMap) {
        super();
        this.dm = dm;
        this.machineTaskProvider = machineTaskProvider;
        this.sm = sm;
        this.transactionHandlerMap = transactionHandlerMap;
        this.enterProvider = enter;
        this.exitProvider = exit;
    }

    @Override
    public StorageManager getSm() {
        return sm;
    }

    @Override
    public void start(ProcessDescriptor p, StateTransition<ActivityProcess> onDone, EventBus bus) {
        // GWT.log(new JSONObject((JavaScriptObject) parameter).toString());
        JsProcessDescriptor paramenter = (JsProcessDescriptor) p;
        JsArrayString values = paramenter.getProcessStepsArray();
        JsFilterData filter = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, values);
        ProcessStepsCallback callback = new ProcessStepsCallback(onDone, values);
        sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), ProcessTaskDescriptor.CATALOG, filter, callback);

    }

    @Override
    public void assembleActivityProcess(JsArray<JsProcessTaskDescriptor> processSteps, StateTransition<ActivityProcess> onDone) {

        changeNavigateTransactionTypes(processSteps);
        ActivityProcess regreso = assembleActivityProcess(processSteps);
        onDone.setResultAndFinish(regreso);
    }

    private ActivityProcess assembleActivityProcess(JsArray<JsProcessTaskDescriptor> processSteps) {
        ActivityProcess regreso = new ActivityProcessImpl();
        InitializeActivityContext enter = enterProvider.get();
        enter.setApplicationItem(applicationItem);
        ReadNextPlace exit = exitProvider.get();

        regreso.addState(enter);

        assembleNativeProcess(regreso, processSteps);

        regreso.addState(exit);
        exit.setApplicationItem(applicationItem);
        return regreso;
    }

    @Override
    public ActivityProcess wrappProcess(Process<?, ?> wrapped) {
        ActivityProcess regreso = new ActivityProcessImpl();
        InitializeActivityContext enter = enterProvider.get();
        ReadNextPlace exit = exitProvider.get();

        regreso.addState(enter);
        regreso.addAll(wrapped);
        regreso.addState(exit);

        exit.setApplicationItem(applicationItem);
        return regreso;
    }

    @Override
    public void assembleNativeProcess(Process<?, ?> regreso, JsArray<JsProcessTaskDescriptor> processSteps) {

        JsProcessTaskDescriptor step;

        int numberOfSteps = processSteps.length();
        // GWT.log("\t"+new JSONObject(processSteps).toString());
        for (int i = 0; i < numberOfSteps; i++) {
            step = processSteps.get(i);
            // GWT.log("\t"+new JSONObject(step).toString());
            presentJob(regreso, step);

        }
    }


    /**
     * "navigate" isn't a real transaction type, so it must be catched and
     * processed
     *
     * @param processSteps
     */
    private void changeNavigateTransactionTypes(JsArray<JsProcessTaskDescriptor> processSteps) {
        JsProcessTaskDescriptor activityDescriptor;
        for (int i = 0; i < processSteps.length(); i++) {
            activityDescriptor = processSteps.get(i);
            String transactionType = activityDescriptor.getTransactionType();
            // TODO extract a task rewriting service that performs this task in
            // a more extensible manner via injection
            // a navigation transaction is a current place child selection
            // transaction
            if (ProcessTaskDescriptor.NAVIGATE_COMMAND.equals(transactionType)) {
                transactionType = ProcessTaskDescriptor.SELECT_COMMAND;
                // activityDescriptor.setTransactionType(transactionType);
                activityDescriptor.setCatalogId(ApplicationItem.CATALOG);
                // TODO READ A PROPERTY and set this flag on start user
                // interaction so custom navigators can be made
                // TODO use COnfigurationContantsImpl
                activityDescriptor.setCurrentPlaceNavigationFlag(true);
                activityDescriptor.addProperty(ContentBrowser.WIDGET, "layout");
                activityDescriptor.addProperty(ContentBrowser.COMMIT_ON_SELECT, "true");
                activityDescriptor.addProperty("autoMargin", "61.8%");
                applicationItem.setOutputHandler(GoToCommand.COMMAND);
            }

        }
    }

    @Override
    public void setContext(ProcessContextServices context) {
    }

    @Override
    public void setApplicationItem(ApplicationItem applicationItem) {
        // FIXME pass as a parameter each time a process is assembled, not as a
        // global, which it clearly isnt
        this.applicationItem = (JsApplicationItem) applicationItem;
    }

    @Override
    public void loadProcess(String processId, StateTransition<List<JsProcessDescriptor>> transactionInfoCallback) {
        JsFilterData filtercriteria = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, processId);
        JsArray<JsArrayString> joins = getJoinsArray(ProcessTaskDescriptor.CATALOG, TaskToolbarDescriptor.CATALOG, WruppleActivityAction.CATALOG,
                JsCatalogEntry.ID_FIELD);
        filtercriteria.setJoins(joins);
        sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), ProcessDescriptor.CATALOG, filtercriteria, transactionInfoCallback);
    }

    private native JsArray<JsArrayString> getJoinsArray(String task, String toolbar, String action, String idField) /*-{
        return [ [ task, idField, "processSteps" ],
				[ toolbar, idField, "toolbars" ],
				[ action, idField, "userActions" ] ];
	}-*/;

    @Override
    public void loadAndAssembleProcess(String processId, StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback) {

        StateTransition processCallback = new ProcessCallback(transactionInfoCallback);
        loadProcess(processId, processCallback);

    }

    class ProcessStepsCallback extends DataCallback<List<JsProcessTaskDescriptor>> {
        StateTransition<ActivityProcess> onDone;
        StateTransition<Process<JavaScriptObject, JavaScriptObject>> switchableCallback;
        private JsArrayString values;
        private boolean swtichable;

        public ProcessStepsCallback(StateTransition<Process<JavaScriptObject, JavaScriptObject>> switchableCallback, JsArrayString values, boolean swtichable) {
            super();
            this.switchableCallback = switchableCallback;
            this.values = values;
            this.swtichable = swtichable;
        }

        public ProcessStepsCallback(StateTransition<ActivityProcess> onDone, JsArrayString values) {
            super();
            this.onDone = onDone;
            this.values = values;
            swtichable = false;
        }

        @Override
        public void execute() {
            JsArray<JsProcessTaskDescriptor> array = JavaScriptObject.createArray().cast();
            String value;
            JsProcessTaskDescriptor desc;
            for (int i = 0; i < values.length(); i++) {
                value = values.get(i);
                desc = find(value, result);
                if (desc != null) {
                    array.push(desc);
                }
            }
            if (onDone == null || swtichable) {
                Process<JavaScriptObject, JavaScriptObject> process = new SequentialProcess<JavaScriptObject, JavaScriptObject>();
                assembleNativeProcess(process, array);
                switchableCallback.setResultAndFinish(process);
            } else {
                assembleActivityProcess(array, onDone);
            }

        }

        private JsProcessTaskDescriptor find(String value, List<JsProcessTaskDescriptor> result) {
            String curr;
            JsCatalogKey cast;
            for (JsProcessTaskDescriptor task : result) {
                cast = task.cast();
                curr = cast.getId();
                if (value.equals(curr)) {
                    return task;
                }
            }
            return null;
        }

    }

    class ProcessCallback extends DataCallback<List<JsProcessDescriptor>> {

        private StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback;

        public ProcessCallback(StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback) {
            this.transactionInfoCallback = transactionInfoCallback;
        }

        @Override
        public void execute() {
            if (result == null || result.isEmpty()) {
                throw new IllegalArgumentException("404");
            }
            JsProcessDescriptor parameter = result.get(0);
            JsArrayString values = parameter.getProcessStepsArray();
            JsFilterData filter = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, values);
            DataCallback<List<JsProcessTaskDescriptor>> callback = new ProcessStepsCallback(transactionInfoCallback, values, true);
            sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), ProcessTaskDescriptor.CATALOG, filter, callback);
        }

    }

}
