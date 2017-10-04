package com.wrupple.muba.desktop.client.services.command.impl;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.command.CatalogCommandService;
import com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class CatalogCommandServiceImpl extends ContextServicesNativeApiBuilder implements CatalogCommandService {

	private StorageManager cds;

	@Inject
	public CatalogCommandServiceImpl(CatalogPlaceInterpret placeInterpret, TransactionalActivityAssembly assembly, StorageManager cds) {
		super(placeInterpret, assembly);
		this.cds = cds;
	}
	class CreateCallBack extends DataCallback<JsTransactionApplicationContext> {
		StorageManager sm;

		public CreateCallBack(StorageManager sm) {
			super();
			this.sm = sm;
		}

		@Override
		public void execute() {
			JsCatalogEntry entry = result.getUserOutput();
			sm.create(host, domain, type, entry, new ReadCallback());
		}

	}
	
	class ListUnWrappingCallback extends DataCallback<List<JsCatalogEntry>> {

		@Override
		public void execute() {
			JsArray<JsCatalogEntry> regreso = JsArrayList.unwrap(result);
			processParameters.setUserOutput(regreso);
			callback.setResultAndFinish(processParameters);
		}

	}
	
	class ReadCallback extends DataCallback<JsCatalogEntry>{

		@Override
		public void execute() {
			processParameters.setUserOutput(result);
			callback.setResultAndFinish(processParameters);
		}
		
	}
	

	class UpdateCallBack extends DataCallback<JsTransactionApplicationContext> {
		StorageManager sm;

		public UpdateCallBack(StorageManager sm) {
			super();
			this.sm = sm;
		}

		@Override
		public void execute() {
			JsCatalogEntry entry = result.getUserOutput();
			sm.update(host, domain, type, entryId, entry, new ReadCallback());
		}

	}

	private ProcessContextServices context;
	private StateTransition<JsTransactionApplicationContext> callback;
	private EventBus bus;
	private JsTransactionApplicationContext processParameters;

	private String action;
	private String type;
	private String entryId;
	private String factoryMethod;
	private String providerField;
	private JavaScriptObject properties;
	private String host,domain;

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext processParameters, StateTransition<JsTransactionApplicationContext> callback) {
		this.type = processParameters.getTaskDescriptor().getCatalogId();
		this.properties = properties;
		this.processParameters = processParameters;
		this.host = processContext.getDesktopManager().getCurrentActivityHost();
		this.domain = processContext.getDesktopManager().getCurrentActivityDomain();
		this.context = processContext;
		this.callback = callback;
		this.bus = eventBus;
	}

	@Override
	public void execute() {

		if (entryId == null) {
			JsCatalogEntry userOutput = processParameters.getUserOutput();
			if (userOutput != null) {
				if (GWTUtils.isArray(userOutput)) {
					JsArray<JsCatalogEntry> arroutput = userOutput.cast();
					userOutput = arroutput.get(0);
				}

				entryId = userOutput.getId();
			}
		}
		if (type == null) {
			JsCatalogEntry userOutput = processParameters.getUserOutput();
			if (userOutput != null) {
				if (GWTUtils.isArray(userOutput)) {
					JsArray<JsCatalogEntry> arroutput = userOutput.cast();
					userOutput = arroutput.get(0);
				}

				type = userOutput.getCatalog();
			}
		}

		cds.loadCatalogDescriptor(host, domain, type, new DataCallback<CatalogDescriptor>() {

			@Override
			public void execute() {

				StorageManager sm = context.getStorageManager();
				if (CatalogActionRequest.CREATE_ACTION.equals(action)) {
					getEntry(new CreateCallBack(sm), result);
				} else if (CatalogActionRequest.READ_ACTION.equals(action)) {
					if (entryId == null) {
						JsFilterData filter = getFilterData();
						StateTransition<List<JsCatalogEntry>> wrappingCallback = new ListUnWrappingCallback();
						sm.read(host, domain, type, filter, wrappingCallback);
					} else {
						sm.read(host, domain, type, entryId, new ReadCallback());
					}
				} else if (CatalogActionRequest.WRITE_ACTION.equals(action)) {
					getEntry(new UpdateCallBack(sm), result);

				} else if (CatalogActionRequest.DELETE_ACTION.equals(action)) {
					sm.delete(host, domain, type, entryId, new ReadCallback());
				}
			}
		});

	}


	

	private JsFilterData getFilterData() {
		return processParameters.getFilterData();
	}

	private void getEntry(StateTransition<JsTransactionApplicationContext> onDone, CatalogDescriptor descriptor) {
		// TODO this should be a service map
		if (providerField != null) {
			
			if ("getterFields".equals(providerField)) {
				//gathers values for all fields of the catalog to create, from the task.getProducedField()s of the current context
				
				JsCatalogEntry userOutput = JsCatalogEntry.createCatalogEntry(descriptor.getCatalogId());
				Collection<FieldDescriptor> fields = descriptor.getOwnedFieldsValues();
				JavaScriptObject o;
				JsArray<JsCatalogEntry> earr;
				JsCatalogKey e;
				String key;
				String fieldId;
				JsArrayString keys;
				for (FieldDescriptor field : fields) {
					fieldId = field.getFieldId();
					if (field.isKey()) {
						if (field.isMultiple()) {
							o = GWTUtils.getAttributeAsJavaScriptObject(processParameters, fieldId + CatalogEntry.MULTIPLE_FOREIGN_KEY);
							if (o != null) {
								earr = o.cast();
								keys = JavaScriptObject.createArray().cast();
								for (int i = 0; i < earr.length(); i++) {
									e = earr.get(i);
									key = e.getId();
									keys.push(key);
								}
								GWTUtils.setAttribute(userOutput, fieldId, keys);
							}
						} else {
							o = GWTUtils.getAttributeAsJavaScriptObject(processParameters, fieldId + CatalogEntry.FOREIGN_KEY);
							if (o != null) {
								e = o.cast();
								key = e.getId();
								GWTUtils.setAttribute(userOutput, fieldId, key);
							}
						}
					} else {
						if (field.isMultiple()) {
							setAttrivute(userOutput, fieldId, processParameters, fieldId + CatalogEntry.MULTIPLE_FOREIGN_KEY);
						} else {
							setAttrivute(userOutput, fieldId, processParameters, fieldId + CatalogEntry.FOREIGN_KEY);

						}
					}
				}
				processParameters.setUserOutput(userOutput);
			} else {
				//user the provider field to get a fully constructed entry
				JavaScriptObject o = GWTUtils.getAttributeAsJavaScriptObject(processParameters, providerField);
				processParameters.setUserOutput(o);
			}
			onDone.setResult(processParameters);
		} else if (factoryMethod != null) {
			invokeJavaScriptFactoryexecute("factory_" + factoryMethod, onDone);
		}
	}

	private native void setAttrivute(JsCatalogEntry userOutput, String fieldId, JsTransactionApplicationContext processParameters, String string) /*-{
		useroutput[fieldId] = processParameters[string];
	}-*/;

	private void invokeJavaScriptFactoryexecute(String functionName, StateTransition<JsTransactionApplicationContext> onDone) {
		JavaScriptObject contextServices = createContextServices(services);
		JavaScriptObject callbackFunction = createTransactionCallbackFunction(onDone);
		invoke(functionName, processParameters, contextServices, callbackFunction);
	}

	private native void invoke(String functionName, JavaScriptObject contextParameters, JavaScriptObject contextServices, JavaScriptObject callbackFunction) /*-{
		var myFunc = $wnd["scoped_" + functionName];
		myFunc(contextParameters, contextServices, callbackFunction);
	}-*/;

	public void setCatalog(String type) {
		this.type = type;
	}

	public void setEntry(String entryId) {
		this.entryId = entryId;
	}


	public void setAction(String action) {
		this.action = action;
	}

	public void setProviderField(String providerField) {
		this.providerField = providerField;
	}

	public void setFactoryMethod(String factoryMethod) {
		this.factoryMethod = factoryMethod;
	}

}
