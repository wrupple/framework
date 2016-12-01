package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface ServiceBus {
	
	char propertyDelimiter='=';
	
	public void excecuteCommand(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext, JsTransactionActivityContext processParameters, StateTransition<JsTransactionActivityContext> callback);
	
	public void excecuteCommand(CommandService service, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionActivityContext processParameters,
			StateTransition<JsTransactionActivityContext> callback);

	public void parseOutput(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext, JsTransactionActivityContext processParameters,
			StateTransition<DesktopPlace> onDone);
	
	public ServiceDictionary<?> getServiceDictionary(String dictionary);

}
