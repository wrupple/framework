package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.editors.RichTextEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.RichTextCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public class RichTextFieldProvider implements CatalogFormFieldProvider {

	CatalogUserInterfaceMessages msgs;
	
	@Inject
	public RichTextFieldProvider(CatalogUserInterfaceMessages msgs) {
		super();
		this.msgs = msgs;
	}



	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode)  {
		Cell<String> regreso;
		
		String name = msgs.richTextEditingProcess();
		Provider<Process<String, String>> processProvider=new Provider<Process<String,String>>() {
			
			@Override
			public Process<String, String> get() {
				RichTextEditor wrap = new RichTextEditor();
				String name = msgs.richTextEditingProcess();
				Process<String, String> process = SequentialProcess.wrap(wrap, wrap, name);
				return process;
			}
		};
		regreso = new RichTextCell(bus,contextServices,contextParameters, d,mode,processProvider, name);
		return regreso;
	}

}
