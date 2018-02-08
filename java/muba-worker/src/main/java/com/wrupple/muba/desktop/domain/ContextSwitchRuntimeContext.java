package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

public interface ContextSwitchRuntimeContext extends ServiceContext {


    ContextSwitch getContextSwitch();


    ContextSwitchRuntimeContext intialize(RuntimeContext requestContext);


    //StorageManager getStorageManager();

}
