package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ExplicitIntent;

public interface WorkCompleteEvent extends BusinessIntent {

    final String CATALOG = "WorkCompleteEvent";

    Workflow getHandleValue();

    Task getTaskDescriptorValue();
}
