package com.wrupple.muba.desktop.client.services.logic;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.shared.services.PeerManager;

public interface HeadlessProcessContext {

	StorageManager getStorageManager();

	ProcessManager getProcessManager();

	public PeerManager getPeerManager();
	
	public EventBus getEventBus();
	
	ServiceBus getServiceBus();
	
	Process<?, ?> getProcess();
	
}
