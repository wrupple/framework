package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.StateTransition;

public interface ContextSwitchRuntimeContext extends ServiceContext {


    ContextSwitch getContextSwitch();


    ProcessManager getProcessManager();


    StorageManager getStorageManager();

}
