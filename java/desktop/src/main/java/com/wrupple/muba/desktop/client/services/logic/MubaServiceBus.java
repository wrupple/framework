package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.factory.dictionary.DictionaryRegistry;
import com.wrupple.muba.desktop.client.factory.dictionary.OutputHandlerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.ServiceMap;
import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class MubaServiceBus implements ServiceBus {

	private ServiceMap commandRegistry;
	private OutputHandlerMap outputHandlerRegistry;
	private DictionaryRegistry serviceLocator;

	@Inject
	public MubaServiceBus(OutputHandlerMap outputHandlerRegistry,
			ServiceMap services,DictionaryRegistry serviceLocator) {
		super();
		this.outputHandlerRegistry = outputHandlerRegistry;
		this.commandRegistry = services;
		this.serviceLocator=serviceLocator;
	}

	@Override
	public void excecuteCommand(String command, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback) {

		
		
		setCommand(properties,command);
		
		CommandService service = commandRegistry.getConfigured(properties, processContext, eventBus, processParameters);
		
		excecuteCommand(command,service, properties, eventBus, processContext, processParameters, callback);
	}
	private native void setCommand(JavaScriptObject properties,  String command) /*-{
		properties.command=command;
	}-*/;

	public void excecuteCommand( CommandService service, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback) {
		excecuteCommand(null,service, properties, eventBus, processContext, processParameters, callback);
	}
	
	private void excecuteCommand(String command, CommandService service, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback) {

		
		if (callback == null) {
			callback = DataCallback.nullCallback();
		}

		service.prepare(command, properties, eventBus, processContext,
				processParameters, callback);
		service.execute();
	}
	@Override
	public void parseOutput(String rawCommand, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<DesktopPlace> callback) {

		String command = rawCommand;
		if (rawCommand.contains(" ")) {
			command = rawCommand.split(" ")[0];
		}
		GWTUtils.setAttribute(properties, outputHandlerRegistry.getPropertyName(), command);
		OutputHandler service = outputHandlerRegistry.getConfigured(properties, processContext, eventBus, processParameters);
		
		assert service != null : "No command '" + rawCommand + "' found";

		service.prepare(rawCommand, properties, eventBus, processContext,
				processParameters, callback);
		service.execute();
	}

	public ServiceDictionary<?> getServiceDictionary(String dictionary){
		ServiceDictionary<?> regreso = this.serviceLocator.get(dictionary);
		if(regreso==null){
			throw new IllegalArgumentException("not a registered service dictionary: "+dictionary);
		}
		return regreso;
	}
	
	

	
}
