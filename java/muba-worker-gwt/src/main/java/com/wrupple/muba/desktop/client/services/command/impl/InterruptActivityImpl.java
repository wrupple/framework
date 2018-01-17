package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.command.InterruptActivity;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsNotification;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.domain.CatalogDescriptor;

public class InterruptActivityImpl  implements InterruptActivity {

	
	private JavaScriptObject properties;
	private ProcessContextServices processContext;
	private JsTransactionApplicationContext contextParameters;
	private String[] targetActivity;
	
	@Inject
	public InterruptActivityImpl(){
	}

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext contextParameters, StateTransition<JsTransactionApplicationContext> callback) {
		this.properties=properties;
		this.contextParameters=contextParameters;
		this.processContext=processContext;
	}

	@Override
	public void execute() {
		final JsNotification output = StandardActivityCommand.getUserOutputEntry(contextParameters.getUserOutput());
		final PlaceController pc = processContext.getPlaceController();
		DesktopPlace current = (DesktopPlace) pc.getWhere();
		final DesktopPlace place = StandardActivityCommand.determineExplicitPlaceIntentArguments(targetActivity, output, current, true);
		
		if(output==null||output.getCatalog()==null){
			pc.goTo(place);
		}else{
			DesktopManager dm = processContext.getDesktopManager();
			processContext.getStorageManager().loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), output.getCatalog(), new DataCallback<CatalogDescriptor>() {
				@Override
				public void execute() {
					StandardActivityCommand.determineFieldUrlParameters(result,place,properties,output);
					pc.goTo(place);
				}
			});
		}
		
		
	}
	public void setTargetActivity(String targetActivity) {
		this.targetActivity = targetActivity.split(com.wrupple.muba.desktop.shared.services.UrlParser.TOKEN_SEPARATOR);
	}

}
