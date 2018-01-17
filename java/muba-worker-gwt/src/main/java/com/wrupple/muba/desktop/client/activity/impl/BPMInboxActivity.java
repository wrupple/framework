package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.domain.Notification;
import com.wrupple.muba.cms.client.services.impl.SimpleContentManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.worker.client.activity.SequentialActivity;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;

public class BPMInboxActivity extends AbstractActivity implements SequentialActivity {

	@Inject
	public BPMInboxActivity(ProcessManager pm, DesktopManager dm, PlaceController pc, TransactionalActivityAssembly assembly, DesktopTheme theme, ActivityVegetateEventHandler vegetateHandler) {
		super(pm, dm, pc, assembly, vegetateHandler);
		/*super.overridenProcessSteps = JavaScriptObject.createArray().cast();
		registerFunction();
		JsProcessTaskDescriptor putFiltersAndSuch =JavaScriptObject.createObject().cast();
		putFiltersAndSuch.setMachineTaskCommandName("scoped_BPMInboxActivity");
		overridenProcessSteps.push(putFiltersAndSuch);*/
		JsArrayString options = JsArrayString.createArray().cast();
		options.push("entry=false");
		super.overridenProcessSteps = SimpleContentManager.defaultzSelectionProcess(false, false, theme, Notification.CATALOG);
		overridenProcessSteps.get(0).addProperty("entry", "false");
		//FIXME cambiar status de la tarea

	}
	public native void registerFunction() /*-{
		$wnd["scoped_BPMInboxActivity"] = function(contextParameters,contextServices, callback) {
				 contextParameters.filter = {filters:[],order:[{ascending:false,field:"timestamp"}]};	
		};
	}-*/;

}
