package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ExplicitIntent;

public interface WorkCompleteEvent extends ManagedObject, ExplicitIntent {

    final String CATALOG = "WorkCompleteEvent";

    Workflow getHandleValue();

    ProcessTaskDescriptor getTaskDescriptorValue();
}
