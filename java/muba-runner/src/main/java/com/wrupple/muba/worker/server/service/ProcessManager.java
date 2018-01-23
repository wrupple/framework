package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.worker.domain.ApplicationState;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     *
     * @param startingState
     * @param thread
     * @return
     */
    ApplicationState acquireContext(Workflow startingState, RuntimeContext thread) throws Exception;


    Solver getSolver();

    ApplicationState requirereContext(Object existingApplicationStateId, RuntimeContext thread) throws Exception;


    ContainerState getContainer(RuntimeContext parent);

    void setContainer(ContainerState request, RuntimeContext parent);

    void setContainer(ContainerState request, SessionContext parent);

}