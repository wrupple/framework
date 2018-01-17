package com.wrupple.muba.desktop.client.activity.process.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionActivityProcess;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionProcess;
import com.wrupple.muba.desktop.client.services.logic.impl.SingletonListSelector;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;

public class CatalogTypeSelectionActivityProcessImpl extends
		SequentialProcess<DesktopPlace, DesktopPlace> implements
		CatalogTypeSelectionActivityProcess {

	
	@Inject
	public CatalogTypeSelectionActivityProcessImpl(CatalogTypeSelectionProcess process) {
		addAll(process);
		add(new SingletonListSelector<DesktopPlace>());
	}

	

}
