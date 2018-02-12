package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;
public class ParentSelectionField extends Composite implements HasValue<String> {
	
	class ChangeParentHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			Process<JsTransactionApplicationContext, JsTransactionApplicationContext> process = cms.getSelectionProcess(processServices, false, false);
			JsTransactionApplicationContext input = JsTransactionApplicationContext.createObject().cast();
			processServices.getProcessManager().processSwitch(process, catalogId, input, new SelectionCallback(), processServices);
		}
		
	}
	
	class SelectionCallback extends DataCallback<JsTransactionApplicationContext>{

		@Override
		public void execute() {
			JsArray<JsCatalogEntry> selectionArr = result.getUserOutput().cast();
			JsCatalogKey selection = selectionArr.get(0);
			setValue(selection.getId());
			Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
			String fieldId;
			JSONObject o = new JSONObject(selection);
			for(FieldDescriptor field : fields){
				fieldId = field.getFieldId();
				editor.setFieldValue(fieldId,o.get(fieldId) );
			}
		}
		
	}
	
	
	class DescriptorLoadedCallback extends DataCallback<CatalogDescriptor>{
		String message;
		
		public DescriptorLoadedCallback(String message) {
			super();
			this.message = message;
		}

		@Override
		public void execute() {
			catalog=result;
			button.setText(message);
			button.addClickHandler(new ChangeParentHandler());
		}
		
	}

	private String catalogId;
	private ContentManager<JsCatalogEntry> cms;
	private CatalogDescriptor catalog;
	private ProcessContextServices processServices;
	private CatalogEditor<? extends JavaScriptObject> editor;
	private String value;
	private Button button;

	public ParentSelectionField(String host,String domain,String catalogParentId,
			StorageManager catalogService,
			ContentManager<JsCatalogEntry> parentCatalogManager,
			ProcessContextServices processServices, CatalogEditor<? extends JavaScriptObject> editor,String message) {
		super();
		this.catalogId=catalogParentId;
		this.cms=parentCatalogManager;
		this.processServices=processServices;
		this.editor=editor;
		catalogService.loadCatalogDescriptor(host, domain, catalogParentId, new DescriptorLoadedCallback(message));
		button = new Button();
		initWidget(button);
	}

	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.setValue(value, true);
		
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		this.value=value;
		if(fireEvents){
			ValueChangeEvent.fire(this, value);
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
