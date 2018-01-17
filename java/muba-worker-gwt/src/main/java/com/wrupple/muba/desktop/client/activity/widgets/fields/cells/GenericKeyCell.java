package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.process.state.ContentLoadingState;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.CatalogKeyTemplates;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;

public class GenericKeyCell extends AbstractDelegatingEditableField<String> {

	private CatalogKeyTemplates template;
	private ContentManagementSystem cms;
	private Provider<ContentLoadingState> contentLoaderProvider;

	public GenericKeyCell(EventBus bus, ProcessContextServices contextualServices, JavaScriptObject contextualParameters, FieldDescriptor d,
			CatalogAction mode, ContentManagementSystem cms,Provider<ContentLoadingState> contentLoadingState) {
		super(bus,contextualServices,contextualParameters,d,mode);
		template = GWT.create(CatalogKeyTemplates.class);
		this.cms = cms;
		this.contentLoaderProvider=contentLoadingState;
	}

	@Override
	protected void renderAsInput(
			com.google.gwt.cell.client.Cell.Context context, String value,
			SafeHtmlBuilder sb, FieldData<String> viewData) {
		if(value==null){
			value="";
		}
		SafeHtml output ;
		if(mode==CatalogAction.DELETE){
			output = template.keyDelete(value);
		}else{
			output = template.keyInput(value);
		}
		sb.append(output);
	}

	@Override
	protected void renderReadOnly(
			com.google.gwt.cell.client.Cell.Context context, String value,
			SafeHtmlBuilder sb, FieldData<String> viewData) {
		if(value==null){
			value="";
		}
		
		SafeHtml output = template.keyOutput(value);
		sb.append(output);
	}

	@Override
	protected String getCurrentInputValue(Element parent, boolean isEditing) {
		if(isEditing){
			InputElement input = parent.getFirstChildElement().cast();
			return input.getValue();
		}else{
			SpanElement span = parent.getFirstChildElement().cast();
			return span.getInnerText();
		}
	}

	@Override
	protected String getProcessLocalizedName() {
		return super.fieldDescriptor.getForeignCatalogName();
	}

	@Override
	protected Process<String, String> getDelegateProcess() {
		String catalog = fieldDescriptor.getForeignCatalogName();
		ContentManager<JsCatalogEntry> manager = cms.getContentManager(catalog);
		
		Process editingProcess;
		
		switch (mode) {
			case CREATE:
				// modify foreign entry
				
				editingProcess = manager.getEditingProcess(CatalogAction.UPDATE, getBus(), contextServices);
				break;
			default:
				editingProcess = manager.getEditingProcess(mode, getBus(), contextServices);
				break;
		}
		Process<String, String> regreso = wrappEditingProcess(editingProcess);
		return regreso;
	}

	private Process<String, String> wrappEditingProcess(Process<JsCatalogEntry, JsCatalogEntry> editingProcess) {
		
		ContentLoadingState loadingState = contentLoaderProvider.get();
		State<String,CatalogProcessDescriptor> keyProcessingState = createKeyProcessingState();
		State<JsCatalogEntry,String> keyExtractingState = createKeyExtractingState();
		
		Process<String, String> delegateProcess = buildDelegateProcess(keyProcessingState,loadingState,editingProcess,keyExtractingState);
		return delegateProcess;
	}

	
	private  SequentialProcess<String, String> buildDelegateProcess(
			State<String, CatalogProcessDescriptor> keyProcessingState,
			ContentLoadingState loadingState,
			Process<JsCatalogEntry, JsCatalogEntry>  editingProcess,
			State<JsCatalogEntry, String> keyExtractingState) {
		SequentialProcess<String, String> regreso = new SequentialProcess<String, String>();
		regreso.add(keyProcessingState);
		regreso.add(loadingState);
		regreso.addAll(editingProcess);
		regreso.add(keyExtractingState);
		return regreso;
	}

	private State<JsCatalogEntry, String> createKeyExtractingState() {
		return new State<JsCatalogEntry, String>(){
			@Override
			public void start(JsCatalogEntry parameter,
					StateTransition<String> onDone, EventBus bus) {
				String key = null;
				if(parameter!=null){
					key = parameter.getId();
				}
				onDone.setResultAndFinish(key);
			}
		};
	}

	private State<String, CatalogProcessDescriptor> createKeyProcessingState() {
		final String catalog = super.fieldDescriptor.getForeignCatalogName();
		return new State<String, CatalogProcessDescriptor>(){
			@Override
			public void start(String parameter,
					StateTransition<CatalogProcessDescriptor> onDone,
					EventBus bus) {
				CatalogProcessDescriptor regreso = new CatalogProcessDescriptor();
				
				regreso.setFilterData(JsFilterData.newFilterData());
				regreso.setSelectedType(catalog);
				regreso.setSelectedValueIdd(parameter);
				
				onDone.setResultAndFinish(regreso);
			}
		};
	}
	


}
