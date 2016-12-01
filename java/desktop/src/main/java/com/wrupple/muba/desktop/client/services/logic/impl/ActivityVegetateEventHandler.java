package com.wrupple.muba.desktop.client.services.logic.impl;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.process.DesktopAuthenticationProcess;
import com.wrupple.muba.desktop.client.event.NewVegetateRequestEvent;
import com.wrupple.muba.desktop.client.event.VegetateEventHandler;
import com.wrupple.muba.desktop.client.event.VegetateRequestFailureEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestSuccessEvent;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.VegetateUserException;

public final class ActivityVegetateEventHandler extends DataCallback<ActivityProcess> implements VegetateEventHandler{
	private final Provider<DesktopAuthenticationProcess> processProvider;
	private final CatalogUserInterfaceMessages msgs;

	@Inject
	public ActivityVegetateEventHandler(Provider<DesktopAuthenticationProcess> processProvider,CatalogUserInterfaceMessages msgs) {
		super();
		this.msgs=msgs;
		this.processProvider = processProvider;
	}

	@Override
	public void onNewVegetateRequest(NewVegetateRequestEvent e) {
		
	}

	@Override
	public void onRequestSuccessful(VegetateRequestSuccessEvent e) {
		
	}

	@Override
	public void onRequestFailed(final VegetateRequestFailureEvent e) {
		if(result!=null){
			int errorCode = e.getExceptionOverlay() == null ? 0 : e.getExceptionOverlay().getErrorCode();
			handleException(errorCode, e);
		}
	}

	private boolean handleException(int errorCode, VegetateRequestFailureEvent e) {
		switch (errorCode) {
		case VegetateUserException.USER_UNKNOWN:
			return switchAuthenticationProcess(e);
		default:
			return false;
		}
	}

	private boolean switchAuthenticationProcess(final VegetateRequestFailureEvent e) {
		DesktopAuthenticationProcess process = processProvider.get();
		final ProcessContextServices context = result.getContext();
		context.getProcessManager().processSwitch(process, msgs.authentication(), null, new DataCallback<Boolean>() {
			@Override
			public void execute() {
				if(result){
					retry(e,context.getStorageManager());
				}
			}

		}, context);
		return true;
	}

	@Override
	public void execute() {
		//activity loaded event
	}
	
	public static void retry(VegetateRequestFailureEvent e,StorageManager sm) {
		JsCatalogActionRequest action = e.getRequest().cast();
		StateTransition<JavaScriptObject> callback = (StateTransition) e.getCallback();
		sm.getRemoteStorageUnit(e.getHost()).callGenericService(action, callback);
	}
}