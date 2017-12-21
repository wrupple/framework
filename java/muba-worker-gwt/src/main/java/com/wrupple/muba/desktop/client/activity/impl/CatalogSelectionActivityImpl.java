package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.SequentialActivity;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionActivityProcess;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

import javax.inject.Provider;

public class CatalogSelectionActivityImpl extends AbstractActivity
        implements CatalogSelectionActivity, SequentialActivity {

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
