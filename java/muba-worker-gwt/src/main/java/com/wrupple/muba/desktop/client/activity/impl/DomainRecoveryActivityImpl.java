package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.DesktopTreeManagementActivity;
import com.wrupple.muba.desktop.client.activity.DomainRecoveryActivity;
import com.wrupple.muba.desktop.client.activity.widgets.BigFatMessage;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;

import javax.inject.Provider;

public class DomainRecoveryActivityImpl extends AbstractActivity implements DomainRecoveryActivity {
	protected final ProcessManager pm;

	protected final PlaceController pc;

	protected final DesktopManager dm;

	private final Provider<DesktopTreeManagementActivity> dtmap;

	@Inject
	public DomainRecoveryActivityImpl(Provider<DesktopTreeManagementActivity> dtmap,DesktopManager dm,ProcessManager pm, PlaceController pc) {
		super();
		this.dtmap=dtmap;
		this.dm=dm;
		this.pm = pm;
		this.pc = pc;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		//TODO copy all Field Descriptors form server
		if(!dm.isDesktopyConfigured()){
			dtmap.get().start(panel, eventBus);
		}else{
			BigFatMessage message = new BigFatMessage("Everything seems fine");
			panel.setWidget(message);
		}

	}

	
}
