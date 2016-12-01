package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class MultiCheckboxFieldProvider implements CatalogFormFieldProvider {

	@Override
	public Cell<Object> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionActivityContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		//TODO
		return null;
	}

}
