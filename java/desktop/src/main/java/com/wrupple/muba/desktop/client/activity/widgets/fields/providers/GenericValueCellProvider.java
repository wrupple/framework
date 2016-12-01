package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.GenericValueCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class GenericValueCellProvider implements CatalogFormFieldProvider {

	private ContentManagementSystem cms;
	private CatalogUserInterfaceMessages msgs;

	@Inject
	public GenericValueCellProvider(ContentManagementSystem cms,CatalogUserInterfaceMessages msgs) {
		super();
		this.msgs=msgs;
		this.cms = cms;
	}

	@Override
	public Cell<JsCatalogEntry> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionActivityContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		return new GenericValueCell(cms, bus, contextServices, contextParameters, d, mode, msgs);
	}

}
