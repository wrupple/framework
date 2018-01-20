package com.wrupple.muba.worker.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.domain.overlay.JsProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.worker.client.activity.ActivityProcess;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.List;

public interface TransactionalActivityAssembly extends
        State.ContextAware<ProcessDescriptor, ActivityProcess> {

    void setApplicationItem(ApplicationItem applicationItem);

    void loadProcess(String processId,
                     StateTransition<List<JsProcessDescriptor>> transactionInfoCallback);

    void loadAndAssembleProcess(String processId,
                                StateTransition<com.wrupple.muba.bpm.client.services.Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback);

    void assembleActivityProcess(JsArray<JsProcessTaskDescriptor> processSteps,
                                 StateTransition<ActivityProcess> onDone);

    ActivityProcess wrappProcess(Process<?, ?> regreso);

    void assembleNativeProcess(Process<?, ?> regreso, JsArray<JsProcessTaskDescriptor> processSteps);

    StorageManager getSm();

}
