package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.activity.ProcessBuildingActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.worker.client.activity.SequentialActivity;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;

public class ProcessBuildingActivityImpl extends AbstractActivity
        implements ProcessBuildingActivity, SequentialActivity {

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
