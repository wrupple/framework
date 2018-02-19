package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.Contract;

public interface ContextSwitch extends Contract {

    String CATALOG = "ContextSwitch";

    WorkerState getWorkerStateValue();

    void setWorkerStateValue(WorkerState request);
}
