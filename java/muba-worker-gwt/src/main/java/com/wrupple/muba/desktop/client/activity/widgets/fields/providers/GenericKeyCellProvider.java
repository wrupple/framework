package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.process.state.ContentLoadingState;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.GenericKeyCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public class GenericKeyCellProvider implements CatalogFormFieldProvider {

	private ContentManagementSystem cms;
	private Provider<ContentLoadingState> contentLoadingState;

	@Inject
	public GenericKeyCellProvider(ContentManagementSystem cms,
			Provider<ContentLoadingState> contentLoadingState) {
		super();
		this.cms = cms;
		this.contentLoadingState = contentLoadingState;
	}

	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		return new GenericKeyCell(bus, contextServices, contextParameters, d, mode, cms, contentLoadingState);
	}

}
