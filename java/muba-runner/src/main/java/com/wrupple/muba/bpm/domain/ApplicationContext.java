package com.wrupple.muba.bpm.domain;


import com.wrupple.muba.bootstrap.domain.ServiceContext;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ApplicationState,ServiceContext {
    final String CATALOG = "ApplicationContext";

    EventRegistry getServiceBus();

   /* StorageManager getStorageManager();

	ProcessManager getProcessManager();

	public PeerManager getPeerManager();*/

	int getTaskIndex();

}