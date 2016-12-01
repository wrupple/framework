package com.wrupple.muba.desktop.client.activity.impl;

import javax.inject.Provider;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.AbstractSequentialActivity;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionActivityProcess;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public class CatalogSelectionActivityImpl extends AbstractSequentialActivity
		implements CatalogSelectionActivity {

	@Inject
	public CatalogSelectionActivityImpl(ProcessManager webapp,DesktopManager dm,
			PlaceController pc,Provider<CatalogTypeSelectionActivityProcess> process) {
		super(dm, webapp, pc);
		this.process=process;
	}



	Provider<CatalogTypeSelectionActivityProcess> process;

	

	@Override
	public void getActivityProcess(DesktopPlace item,JsApplicationItem app,
			DataCallback<ActivityProcess> callback) {
		callback.setResultAndFinish(process.get());
	}



}
