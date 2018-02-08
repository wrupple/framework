package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.Event;

public interface ContextSwitch extends Event {

    String CATALOG = "ContextSwitch";

    WorkerState getWorkerStateValue();

    void setWorkerStateValue(WorkerState request);
}
