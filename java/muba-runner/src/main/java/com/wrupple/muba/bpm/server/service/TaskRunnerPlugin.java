package com.wrupple.muba.bpm.server.service;

//import com.google.web.bindery.event.shared.EventBus;
//import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
//import com.wrupple.vegetate.client.services.StorageManager;
//import com.wrupple.vegetate.shared.services.PeerManager;

public interface TaskRunnerPlugin extends CatalogPlugin {

	Solver getSolver();

	/*StorageManager getStorageManager();

	ProcessManager getProcessManager();

	public PeerManager getPeerManager();
	
	public EventBus getEventBus();
	
	ServiceBus getServiceBus();
	//of the current thread?
	Process<?, ?> getProcess();*/
	
}
