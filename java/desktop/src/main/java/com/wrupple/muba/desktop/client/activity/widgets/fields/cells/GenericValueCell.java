package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.CatalogKeyTemplates;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class GenericValueCell extends  AbstractDelegatingEditableField<JsCatalogEntry>  {

	private ContentManagementSystem cms;
	private CatalogKeyTemplates template;
	private CatalogUserInterfaceMessages msgs;
	

	public GenericValueCell(ContentManagementSystem cms,EventBus bus,
			ProcessContextServices contextServices,
			JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,CatalogUserInterfaceMessages msgs) {
		super(bus, contextServices, contextParameters, d, mode);
		this.cms=cms;
		this.msgs=msgs;
		template = GWT.create(CatalogKeyTemplates.class);
	}

	@Override
	protected String getProcessLocalizedName() {
		return msgs.browsableViewName(super.fieldDescriptor.getForeignCatalogName());
	}

	@Override
	protected Process<JsCatalogEntry, JsCatalogEntry> getDelegateProcess() {
		/*TODO default behavious used to be when clicking on a foreign value cell, open a detail view of the foreign entry
		 * but this interfrs with elimination of Relations on GenericFieldFactory /  ForeignValueRelationEditor
		 * 
		 * String catalog = super.fieldDescriptor.getForeignCatalogName();
		ContentManager<JsCatalogEntry> manager = cms.getContentManager(catalog);
		Process editingProcess = manager.getEditingProcess(Mode.READ, getBus(), contextServices);
		return editingProcess;*/
		return null;
	}

	@Override
	protected void renderAsInput(
			com.google.gwt.cell.client.Cell.Context context,
			JsCatalogEntry value,
			SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<JsCatalogEntry> viewData) {
		if(value==null){
			
		}else{
			String key=value.getId();
			String name=value.getName();
			if(key==null){
				key = "";
			}
			if(name == null){
				name = value.getStringValue();
			}
			if(name == null){
				name = key;
			}
			SafeHtml output = template.value(key, name);
			sb.append(output);
		}
	}

	@Override
	protected void renderReadOnly(
			com.google.gwt.cell.client.Cell.Context context,
			JsCatalogEntry value,
			SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<JsCatalogEntry> viewData) {
		if(value==null){
			
		}else{
			String key=value.getId();
			String name=value.getName();
			if(key==null){
				key = "";
			}
			if(name == null){
				name = value.getStringValue();
			}
			if(name == null){
				name = key;
			}
			SafeHtml output = template.value(key, name);
			sb.append(output);
		}
		
	}

	@Override
	protected JsCatalogEntry getCurrentInputValue(Element parent, boolean isEditing) {
		JsCatalogEntry regreso = JavaScriptObject.createObject().cast();
		return regreso;
	}
	
	

}
