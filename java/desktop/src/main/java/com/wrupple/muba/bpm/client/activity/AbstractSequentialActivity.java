package com.wrupple.muba.bpm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public abstract class AbstractSequentialActivity extends AbstractActivity implements SequentialActivity {

	public static class SetApplicationStateAndContext extends DataCallback<ActivityProcess> {
		ProcessManager pm;
		AcceptsOneWidget panel;
		EventBus eventBus;
		JsApplicationItem applicationItem;

		public SetApplicationStateAndContext(ProcessManager pm, AcceptsOneWidget panel, EventBus eventBus, JsApplicationItem applicationItem) {
			super();
			this.pm = pm;
			this.panel = panel;
			this.eventBus = eventBus;
			this.applicationItem = applicationItem;
		}

		@Override
		public void execute() {
			pm.setCurrentProcess(applicationItem.getProcessAsId());
			pm.contextSwitch(result, applicationItem, panel, eventBus);
		}

	}

	protected final ProcessManager pm;

	protected final PlaceController pc;

	protected final DesktopManager dm;

	public AbstractSequentialActivity(DesktopManager dm, ProcessManager pm, PlaceController pc) {
		super();
		this.dm = dm;
		this.pm = pm;
		this.pc = pc;
	}

	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		DesktopPlace place = (DesktopPlace) pc.getWhere();
		if (!dm.isDesktopyConfigured()) {
			if(recoverFromMissconfiguredDesktop(place)){
				return;
			}
			
		}
		
		JavaScriptObject o = dm.getApplicationItem(place);

		JsApplicationItem applicationItem ;
		if(o==null){
			applicationItem=null;
		}else{
			applicationItem=o.cast();
		}
		getActivityProcess(place, applicationItem, new SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));
	}

	protected boolean recoverFromMissconfiguredDesktop(DesktopPlace place) {
		DesktopPlace newPlace = new DesktopPlace(DesktopManager.RECOVERY_ACTIVITY);
		newPlace.setFoward(place);
		pc.goTo(newPlace);
		return true;
	}

}
