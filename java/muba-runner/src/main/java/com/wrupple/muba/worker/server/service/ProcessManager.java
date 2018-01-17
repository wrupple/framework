package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.domain.Workflow;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     *
     * @param startingState
     * @param thread
     * @return
     */
    ApplicationState acquireContext(Workflow startingState, SessionContext thread) throws Exception;


    Solver getSolver();

    ApplicationState requirereContext(Object existingApplicationStateId, RuntimeContext session) throws Exception;


}
