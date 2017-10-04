package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.ColorPickerCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class ColorPickerProvider implements CatalogFormFieldProvider {

	@Override
	public Cell<String> createCell(EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters, JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode)  {
		return new ColorPickerCell(bus,contextServices,contextParameters,d,mode);
	}

}
