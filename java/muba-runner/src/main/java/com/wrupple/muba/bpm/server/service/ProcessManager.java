package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     * @param copyFieldValuesFrom
     * @param thread
     * @return
     */
    ApplicationState acquireContext(CatalogEntry copyFieldValuesFrom, RuntimeContext thread);
}
