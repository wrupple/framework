package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.FlowDataPanel;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleFlowLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.AbstractForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.client.services.presentation.ForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class FlowBrowserHandle extends AbstractForeignRelationWidgetHandle implements ForeignRelationWidgetHandle {

	CatalogEntryKeyProvider keyProvider;
	private String cellWrapperClass;
	private boolean inline;
	
	@Inject
	public FlowBrowserHandle(CatalogEntryKeyProvider keyProvider) {
		super();
		this.keyProvider = keyProvider;
	}


	public HasData<JsCatalogEntry> get(Cell<JsCatalogEntry> cell) {
		FlowDataPanel<JsCatalogEntry> regreso = new FlowDataPanel<JsCatalogEntry>(cell, keyProvider, null);
		regreso.setCellWrapperClass(cellWrapperClass);
		regreso.setInline(inline);
		return regreso;
	}
	
	public void setCellWrapperClass(String cellWrapperClass) {
		this.cellWrapperClass = cellWrapperClass;
	}
	
	public void setInline(String layoutDelegate) {
		boolean inline = layoutDelegate == null || WruppleFlowLayoutDataWidget.INLINE_PROP_VALUE.equals(layoutDelegate)||Boolean.parseBoolean(layoutDelegate);
		this.inline = inline;
		
	}


	@Override
	public boolean showAddRelation() {
		return true;
	}


	@Override
	public boolean showRemoveSelectionFromRelation() {
		return true;
	}


	@Override
	public void init(FieldDescriptor field, JavaScriptObject fieldProperties, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices, RelationshipDelegate delegate, GenericDataProvider dataProvider, CatalogAction mode) {
		
	}


}
