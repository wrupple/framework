package com.wrupple.muba.desktop.client.activity.process.impl;

import java.util.List;

import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionProcess;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogTypeSelectionTask;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public class CatalogTypeSelectionProcessImpl extends
		SequentialProcess<DesktopPlace, List<DesktopPlace>> implements
		CatalogTypeSelectionProcess {

	@Inject
	public CatalogTypeSelectionProcessImpl(CatalogSelectionLoader load, CatalogTypeSelectionTask state) {
		super();
		add(load);
		add(state);
	}

	

}
