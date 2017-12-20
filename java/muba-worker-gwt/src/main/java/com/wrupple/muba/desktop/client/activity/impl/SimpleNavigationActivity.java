package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.impl.TransactionalActivity;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.activity.NavigationActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;

public class SimpleNavigationActivity extends TransactionalActivity
		implements NavigationActivity {

	@Inject
	public SimpleNavigationActivity(ProcessManager pm, DesktopManager dm,
			PlaceController pc, TransactionalActivityAssembly assembly, ActivityVegetateEventHandler vegetateHandler) {
		super(pm, dm, pc, assembly, vegetateHandler);
		
		overridenProcessSteps=SimpleContentManager.getNavigateSelectionProcess();
	}
}
