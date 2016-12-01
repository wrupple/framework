package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public class ExitToBrowseActivity implements State<Object, DesktopPlace> {

	String catalog;
	
	public ExitToBrowseActivity(String catalog) {
		super();
		this.catalog = catalog;
	}

	@Override
	public void start(Object parameter, StateTransition<DesktopPlace> onDone,
			EventBus bus) {
		DesktopPlace result = new DesktopPlace(CatalogSelectionActivity.ACTIVITY_ID);
		result.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, catalog);
		onDone.setResultAndFinish(result);
	}

}
