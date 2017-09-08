package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.event.EventBus;

public interface ServiceBus {
    /*
    	char propertyDelimiter='=';

	public void excecuteCommand(String command, JavaScriptObject commandProperties,
                                EventBus eventBus, ProcessContextServices processContext, JsTransactionApplicationContext processParameters, StateTransition<JsTransactionApplicationContext> callback);

	public void excecuteCommand(CommandService service, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback);

	public ServiceDictionary<?> getServiceDictionary(String dictionary);
     */


    public void parseOutput(String command, Object commandProperties,
                            EventBus eventBus, ProcessContextServices processContext, JsTransactionApplicationContext processParameters,
                            StateTransition<DesktopPlace> onDone);


}
