package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.activity.DesktopTreeManagementActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.worker.client.activity.ActivityProcess;
import com.wrupple.muba.worker.client.activity.SequentialActivity;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

public class DesktopTreeManagementActivityImpl extends AbstractActivity
        implements DesktopTreeManagementActivity, SequentialActivity {

	@Inject
	public DesktopTreeManagementActivityImpl(ProcessManager pm,
			DesktopManager dm, PlaceController pc,
			TransactionalActivityAssembly assembly, DesktopTheme theme, ActivityVegetateEventHandler vegetateHandler) {
		super(pm, dm, pc, assembly, vegetateHandler);
		JsArray<JsProcessTaskDescriptor> defaultCreationProcess =SimpleContentManager.getDefaultSelectionProcess(theme, ApplicationItem.CATALOG, false);
		overridenProcessSteps=defaultCreationProcess;
	}
	
	@Override
	public void getActivityProcess(DesktopPlace input, JsApplicationItem actd, DataCallback<ActivityProcess> callback) {
		dm.putPlaceParameter(CatalogActionRequest.CATALOG_ID_PARAMETER, ApplicationItem.CATALOG);
		super.getActivityProcess(input, actd, callback);
	}

	@Override
	protected boolean recoverFromMissconfiguredDesktop() {
		//STOP DEFAULT BEHAVIOUR OF REDIRECTING USER IF DESKTOP IS MISCONFIGURED
		return false;
	}
}
