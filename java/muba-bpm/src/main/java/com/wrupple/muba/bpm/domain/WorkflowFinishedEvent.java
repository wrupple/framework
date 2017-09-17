package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ExplicitIntent;

public interface WorkflowFinishedEvent extends ManagedObject, ExplicitIntent {

    final String CATALOG = "WorkflowFinishedEvent";

    Workflow getApplicationItemValue();

    ProcessTaskDescriptor getTaskDescriptorValue();
}
