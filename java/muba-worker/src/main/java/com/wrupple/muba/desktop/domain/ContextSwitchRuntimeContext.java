package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.worker.server.service.ProcessManager;

public interface ContextSwitchRuntimeContext extends ServiceContext {


    ContextSwitch getContextSwitch();


    ProcessManager getProcessManager();


    StorageManager getStorageManager();


}
