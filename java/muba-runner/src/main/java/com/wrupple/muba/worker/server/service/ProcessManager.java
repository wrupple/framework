package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     *
     * @param startingState
     * @param thread
     * @return
     */
    Solver getSolver();

    WorkerState getWorker(RuntimeContext parent);

    void setWorker(WorkerState request, RuntimeContext parent);

    void setWorker(WorkerState request, SessionContext parent);

}