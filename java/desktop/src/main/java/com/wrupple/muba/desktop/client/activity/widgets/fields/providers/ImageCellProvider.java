package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import javax.inject.Provider;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.process.CanvasDrawingProcess;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.EncodedImageCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class ImageCellProvider implements CatalogFormFieldProvider {

	private final ImageTemplate template;
	private final Provider<CanvasDrawingProcess> nestedProcessProvider;

	@Inject
	public ImageCellProvider(ImageTemplate template, Provider<CanvasDrawingProcess> nestedProcessProvider) {
		super();
		this.template = template;
		this.nestedProcessProvider = nestedProcessProvider;
	}



	@Override
	public Cell<? extends Object> createCell(EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		return new EncodedImageCell(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, d.getName(), template);
	}

}
