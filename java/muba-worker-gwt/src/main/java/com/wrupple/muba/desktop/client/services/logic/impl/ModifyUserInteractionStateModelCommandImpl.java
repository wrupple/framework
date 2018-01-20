package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder;
import com.wrupple.muba.desktop.client.services.logic.ModifyUserInteractionStateModelCommand;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.ModelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class ModifyUserInteractionStateModelCommandImpl extends ContextServicesNativeApiBuilder implements ModifyUserInteractionStateModelCommand {

	class ModelAlterationCallback extends DataCallback<JavaScriptObject> {
		TakesValue dataTarget;
		private JsTransactionApplicationContext contextParameters;

		public ModelAlterationCallback(TakesValue dataTarget, JsTransactionApplicationContext contextParameters) {
			super();
			this.dataTarget = dataTarget;
			this.contextParameters = contextParameters;
		}

		@Override
		public void execute() {
			dataTarget.setValue(result);
			callback.setResultAndFinish(contextParameters);
		}

	}

	@Inject
	public ModifyUserInteractionStateModelCommandImpl(CatalogPlaceInterpret placeInterpret, TransactionalActivityAssembly assembly) {
		super(placeInterpret, assembly);
	}

    TaskContainer panel;

	ModelTransformationConfig config;
	private StateTransition<JsTransactionApplicationContext> callback;
	private JsTransactionApplicationContext contextParameters;
	private String functionName;

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext contextParameters, StateTransition<JsTransactionApplicationContext> callback) {
		this.panel = processContext.getNestedTaskPresenter();
		config = properties.cast();
		services = processContext;
		this.contextParameters = contextParameters;
		this.callback = callback;
	}

	@Override
	public void execute() {
		JavaScriptObject sourceData = config.getSourceData();
        // FIXME have model alteration targets point at any TOOLBAR setRuntimeContext a given
        // type/catalog
		String targetName = config.getTarget();
		String sourceName = config.getSource();
		String postProcess = config.getPostProcess();
		boolean forceFocus = config.isForceFocus();
		// boolean pushFocus = getPushFocus(config);
		if (sourceData == null) {
			sourceData = getDataFromSource(sourceName);
		}

		TakesValue dataTarget = getDataTarget(targetName, forceFocus);
		if (postProcess != null) {
			// TODO sublist? apply filter Data (probably a bad idea)
			// TODO modify Filters of a Browser based on some data
			if ("arrayEnclosed".equals(postProcess)) {
				JsArray<JavaScriptObject> wrapper = JavaScriptObject.createArray().cast();
				wrapper.push(sourceData);
				sourceData = wrapper;
			} else if ("selectIndex".equals(postProcess)) {
				int selectionIndex = config.getPostProcessSelectionIndex();
				JsArray<JavaScriptObject> cast = sourceData.cast();
				sourceData = cast.get(selectionIndex);
			} else {
				throw new IllegalArgumentException("Unknown Data alteration post process: " + postProcess);
			}
		}

		ModelAlterationCallback end = new ModelAlterationCallback(dataTarget, contextParameters);

		if (functionName == null) {
			end.setResultAndFinish(sourceData);
		} else {
			JavaScriptObject contextServices = createContextServices(services);
			JavaScriptObject callbackFunction = createUnsafeCallbackFunction(end);
			invoke(functionName, sourceData, contextParameters, contextServices, callbackFunction);
		}

	}

	private native void invoke(String functionName, JavaScriptObject sourceData, JavaScriptObject contextParameters, JavaScriptObject contextServices,
			JavaScriptObject callbackFunction) /*-{
		var myFunc = $wnd["scoped_modelAlteration_" + functionName];
		myFunc(sourceData, contextParameters, contextServices, callbackFunction);
	}-*/;

	private TakesValue getDataTarget(String targetName, boolean forceFocus) {
		if (targetName == null || "transaction".equals(targetName)) {
			return panel.getTaskContent().getMainTaskProcessor();
		} else {
			Toolbar activityToolbar = panel.getTaskContent().getToolbarById(targetName);
			if (forceFocus && activityToolbar != null) {
				panel.getTaskContent().focusToolbar(targetName);
			}
			return activityToolbar;
		}

	}

	private JavaScriptObject getDataFromSource(String sourceName) {
		TakesValue t;
		if (sourceName == null || "transaction".equals(sourceName)) {
			t =  panel.getTaskContent().getMainTaskProcessor();
		} else {
			Toolbar activityToolbar = panel.getTaskContent().getToolbarById(sourceName);
			t = activityToolbar;
		}
		return (JavaScriptObject) t.getValue();
	}

	@Override
	public void setModelAlterationFunction(String f) {
		this.functionName = f;
	}

}
