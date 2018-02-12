package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.List;

public class CatalogSelectionLoaderImpl implements CatalogSelectionLoader {


	StorageManager service;
	private DesktopManager dm;

	@Inject
	public CatalogSelectionLoaderImpl(StorageManager service,DesktopManager dm) {
		super();
		this.dm=dm;
		this.service = service;
	}

	@Override
	public void start(DesktopPlace parameter,
			StateTransition<List<JsCatalogEntry>> onDone, EventBus bus) {
		service.loadCatalogNames(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), onDone);
	}


}
