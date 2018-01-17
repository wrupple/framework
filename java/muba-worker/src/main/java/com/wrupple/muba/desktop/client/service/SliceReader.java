package com.wrupple.muba.desktop.client.service;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.Application;
import com.wrupple.muba.worker.domain.WorkerLoadOrder;

public interface SliceReader {
    Application getInitialActivity(WorkerLoadOrder request, RuntimeContext parent);
}
