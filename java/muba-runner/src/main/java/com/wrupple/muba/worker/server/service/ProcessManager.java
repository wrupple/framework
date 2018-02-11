package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     *
     * @return
     */
    Solver getSolver();

}