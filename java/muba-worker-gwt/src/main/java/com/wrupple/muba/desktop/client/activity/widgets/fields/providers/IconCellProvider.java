package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.SimpleIconCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class IconCellProvider implements CatalogFormFieldProvider {

	private final ImageTemplate template;

	@Inject
	public IconCellProvider(ImageTemplate template) {
		super();
		this.template = template;
	}

	@Override
	public Cell<? extends Object> createCell(EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		SimpleIconCell cell = new SimpleIconCell(template);
		return cell;
	}

}
