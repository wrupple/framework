package com.wrupple.muba.bpm.server.service;

//import com.google.web.bindery.event.shared.EventBus;
//import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
//import com.wrupple.vegetate.client.services.StorageManager;
//import com.wrupple.vegetate.shared.services.PeerManager;

public interface SolverCatalogPlugin extends CatalogPlugin {

	Solver getSolver();
	
}
