package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.worker.server.service.StateTransition;

public class ExcecuteJavaScriptFuntion extends ContextServicesNativeApiBuilder implements CommandService {

	private StateTransition<JsTransactionApplicationContext> onDone;
	private JsTransactionApplicationContext contextParameters;
	private String functionName;

	/*
	 * INCOKING NATIVE FUNCTIONS IS POTENTIALLY MESSY AND DANGEROUS
	 * DECPRECATE THIS
	 * FIXME :: turn in favor of evaluating functions las those in ephemeral
	 * values, even if they require cycles, flow structure
	 */

	@Inject
	public ExcecuteJavaScriptFuntion(CatalogPlaceInterpret placeInterpret, TransactionalActivityAssembly assembly) {
		super(placeInterpret, assembly);
	}

	JavaScriptObject function;

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext processParameters, StateTransition<JsTransactionApplicationContext> callback) {
		this.onDone = callback;
		this.services = processContext;
		this.contextParameters = processParameters;
		assembly.setApplicationItem((ApplicationItem) processParameters.getApplicationItem());
		function = GWTUtils.getAttributeAsJavaScriptObject(properties, functionName);

	}

	public void setFunction(String factoryMethod) {
		this.functionName = factoryMethod;
	}

	@Override
	public void execute() {
		JavaScriptObject contextServices = createContextServices(services);
		JavaScriptObject callbackFunction = createTransactionCallbackFunction(onDone);
		if (function == null) {
			invoke(functionName, contextParameters, contextServices, callbackFunction);
		} else {
			invoke(function, contextParameters, contextServices, callbackFunction);
		}

	}

	private native void invoke(String functionName, JavaScriptObject contextParameters, JavaScriptObject contextServices, JavaScriptObject callbackFunction) /*-{
		var myFunc = $wnd["scoped_" + functionName];
		myFunc(contextParameters, contextServices, callbackFunction);
	}-*/;

	private native void invoke(JavaScriptObject myFunc, JavaScriptObject contextParameters, JavaScriptObject contextServices, JavaScriptObject callbackFunction) /*-{
		myFunc(contextParameters, contextServices, callbackFunction);
	}-*/;
}
