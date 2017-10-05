package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ExplicitIntent;

public interface WorkflowFinishedIntent extends ManagedObject, ExplicitIntent {

    final String CATALOG = "WorkflowFinishedIntent";

    Workflow getHandleValue();

    ProcessTaskDescriptor getTaskDescriptorValue();
}
