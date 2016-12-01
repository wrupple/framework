package com.wrupple.muba.desktop.client.services.command;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.HumanActivityContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FilterData;

public class ContextServicesNativeApiBuilder {
	private static CatalogPlaceInterpret placeInterpret;
	protected static TransactionalActivityAssembly assembly;

	//FIXME avoid using static fields, make process context aware (ensure this!)
	protected ProcessContextServices services;

	public ContextServicesNativeApiBuilder(
			CatalogPlaceInterpret placeInterpret,
			TransactionalActivityAssembly assembly) {
		super();
		ContextServicesNativeApiBuilder.assembly = assembly;
		ContextServicesNativeApiBuilder.placeInterpret = placeInterpret;
	}

	protected native JavaScriptObject createTransactionCallbackFunction(
			StateTransition<JsTransactionActivityContext> callback) /*-{
		return $entry(function(x) {
			callback.@com.wrupple.muba.desktop.client.activity.process.state.StateTransition::setResultAndFinish(*)(x);
			});
	}-*/;
	
	protected native JavaScriptObject createUnsafeCallbackFunction(
			StateTransition<JavaScriptObject> callback) /*-{
		return $entry(function(x) {
			callback.@com.wrupple.muba.desktop.client.activity.process.state.StateTransition::setResultAndFinish(*)(x);
			});
	}-*/;

	//TODO call command, in nested processes, make sure the right processContext parameters are being passed
	protected native JavaScriptObject createContextServices(
			ProcessContextServices services) /*-{
		var regreso = {};
		regreso.getCurrentPlaceProperty=$entry(@com.wrupple.muba.desktop.domain.HumanActivityContextServices::getCurrentPlaceProperty(Ljava/lang/String;));
		regreso.getCurrentPlaceActivity=$entry(@com.wrupple.muba.desktop.domain.HumanActivityContextServices::getCurrentPlaceActivity());
		regreso.getCurrentPlaceFilterData=$entry(@com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder::getCurrentPlaceFilterData());
		regreso.processSwitch=$entry(@com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder::processSwitch(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;));
		//regreso.wruppleService =$entry(@com.wrupple.base.client.services.impl.ContextServicesNativeApiBuilder::callCommand(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
		return regreso;
	}-*/;



	public static FilterData getCurrentPlaceFilterData() {
		DesktopPlace place = (DesktopPlace) HumanActivityContextServices.SATIC_INSTANCE.getPlaceController().getWhere();
		return placeInterpret.getCurrentPlaceFilterData(place);
	}

	public static void processSwitch(String processId,
			JavaScriptObject contextParameters, JavaScriptObject callback) {
		assembly.loadAndAssembleProcess(processId,
				new ProcessAssembledCallback(callback, HumanActivityContextServices.SATIC_INSTANCE.getEventBus(), contextParameters));
	}

	static class ProcessAssembledCallback
			extends
			DataCallback<com.wrupple.muba.bpm.client.services.Process<JavaScriptObject, JavaScriptObject>> {

		final JavaScriptObject callbackFunction;
		private final EventBus bus;
		private final JavaScriptObject input;

		public ProcessAssembledCallback(JavaScriptObject callbackFunction,
				EventBus bus, JavaScriptObject input) {
			super();
			this.bus = bus;
			this.input = input;
			this.callbackFunction = callbackFunction;
		}

		@Override
		public void execute() {
			// TODO this wont work...
			HumanActivityContextServices.SATIC_INSTANCE.getProcessManager().processSwitch(result, "", input,
					new SwitchedProcessCallback(callbackFunction), null);
		}

	}

	static class SwitchedProcessCallback extends DataCallback<JavaScriptObject> {

		final JavaScriptObject callbackFunction;

		public SwitchedProcessCallback(JavaScriptObject callbackFunction) {
			super();
			this.callbackFunction = callbackFunction;
		}

		@Override
		public void execute() {
			invokeJsFunction(callbackFunction, result);
		}

		private native void invokeJsFunction(JavaScriptObject callbackFunction,
				JavaScriptObject result) /*-{
	callbackFunction(result);
}-*/;

	}

}
