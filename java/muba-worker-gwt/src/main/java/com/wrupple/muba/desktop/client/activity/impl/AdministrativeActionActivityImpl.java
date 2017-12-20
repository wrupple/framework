package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.AbstractSequentialActivity;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.process.impl.ActivityProcessImpl;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.AdministrativeActionActivity;
import com.wrupple.muba.desktop.client.activity.process.state.AdministrativeAction;
import com.wrupple.muba.desktop.client.activity.process.state.impl.ArbitraryActivityExit;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public class AdministrativeActionActivityImpl extends
		AbstractSequentialActivity implements AdministrativeActionActivity {

	
	
	public static class AdminPlaceInterpret implements State<DesktopPlace,JavaScriptObject>{

		@Override
		public void start(DesktopPlace parameter,
				StateTransition<JavaScriptObject> onDone, EventBus bus) {
			JavaScriptObject regreso = JavaScriptObject.createObject();
			onDone.setResultAndFinish(regreso);
		}
		
	}
	
	public static class AdministrativeProcess extends ActivityProcessImpl{

		@Inject
		public AdministrativeProcess(AdministrativeAction admin) {
			super();
			add(new AdminPlaceInterpret());
			addState(admin);
			add(new ArbitraryActivityExit(new DesktopPlace(DesktopLoadingStateHolder.homeActivity)));
		}
		
	}

	private AdministrativeProcess process;
	
	@Inject
	public AdministrativeActionActivityImpl(ProcessManager webapp,
			PlaceController pc,AdministrativeProcess process,DesktopManager dm) {
		super(dm, webapp, pc);
		this.process=process;
	}

	@Override
	public void getActivityProcess(DesktopPlace item,JsApplicationItem app,
			DataCallback<ActivityProcess> callback) {
		callback.setResultAndFinish(process);
	}


}
