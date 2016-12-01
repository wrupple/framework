package com.wrupple.muba.desktop.client.activity.process.state.impl;

import java.util.List;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogIdentification;
import com.wrupple.vegetate.client.services.StorageManager;

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
			StateTransition<List<JsCatalogIdentification>> onDone, EventBus bus) {
		service.loadCatalogNames(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), onDone);
	}


}
