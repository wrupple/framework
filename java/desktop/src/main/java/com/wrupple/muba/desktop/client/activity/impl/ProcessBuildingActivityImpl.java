package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.impl.TransactionalActivity;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.activity.ProcessBuildingActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;

public class ProcessBuildingActivityImpl extends TransactionalActivity
		implements ProcessBuildingActivity {

	@Inject
	public ProcessBuildingActivityImpl(ProcessManager pm, DesktopManager dm,
			PlaceController pc, TransactionalActivityAssembly assembly, DesktopTheme theme, ActivityVegetateEventHandler vegetateHandler) {
		super(pm, dm, pc, assembly, vegetateHandler);
		JsArray<JsProcessTaskDescriptor> createDesktopPlace = SimpleContentManager.getDefaultCreationProcess(theme,ApplicationItem.CATALOG,"createdPlace");
		JsArray<JsProcessTaskDescriptor> selectDesktopPlaceParent = SimpleContentManager.getDefaultSelectionProcess(theme,"PersistentCatalogDescriptor",false);
		addAll(createDesktopPlace,selectDesktopPlaceParent);
		//TODO update selected parent append newly created child (automated task)
		overridenProcessSteps=createDesktopPlace;
	}

	private void addAll(
			JsArray<JsProcessTaskDescriptor> to,
			JsArray<JsProcessTaskDescriptor> from) {
		for(int i =  0; i < from.length(); i++){
			to.push(from.get(i));
		}
	}

}