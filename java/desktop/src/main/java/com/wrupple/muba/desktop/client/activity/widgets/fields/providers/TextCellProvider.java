package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class TextCellProvider implements CatalogFormFieldProvider {

	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionActivityContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode)  {
		//TODO configure javaScript, HTML, and CSS markup
		Cell<String> regreso = null;
		if (mode != CatalogAction.READ) {
			regreso = new TextInputCell();
		} else {
			regreso = new TextCell();
		}
		return regreso;
	}

	
}
