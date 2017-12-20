package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.impl.TransactionalActivity;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.activity.CatalogEntryCreateActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;

public class ContentCreationActivity extends TransactionalActivity implements CatalogEntryCreateActivity {

	@Inject
	public ContentCreationActivity(ProcessManager pm, DesktopManager dm,
			PlaceController pc, TransactionalActivityAssembly assembly,DesktopTheme theme, ActivityVegetateEventHandler vegetateHandler) {
		super(pm, dm, pc, assembly, vegetateHandler);
		overridenProcessSteps=SimpleContentManager.getDefaultCreationProcess(theme);
	}
	
	@Override
	protected boolean recoverFromMissconfiguredDesktop() {
		//STOP DEFAULT BEHAVIOUR OF REDIRECTING USER IF DESKTOP IS MISCONFIGURED
		return false;
	}

}
