package com.wrupple.muba.desktop.client.services.logic;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.vegetate.shared.services.PeerManager;

public interface HeadlessProcessContext {

	StorageManager getStorageManager();

	ProcessManager getProcessManager();

    PeerManager getPeerManager();

    EventBus getEventBus();

    ServiceBus getServiceBus();
	
	Process<?, ?> getProcess();
	
}
