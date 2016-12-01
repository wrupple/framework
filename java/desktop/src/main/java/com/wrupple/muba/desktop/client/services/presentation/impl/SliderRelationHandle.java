package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.LayoutDataPanel.DataWidgetFactory;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.SliderDataPanel;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleIndexedLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.AbstractForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.client.services.presentation.ForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class SliderRelationHandle extends AbstractForeignRelationWidgetHandle implements
		ForeignRelationWidgetHandle {

	private int viewportWidth;
	private int viewportHeight;
	private String cellWrapperClass;
	private String cellClass;
	@Override
	public HasData<JsCatalogEntry> get(Cell<JsCatalogEntry> cell) {
		DataWidgetFactory<JsCatalogEntry> factory = new WruppleIndexedLayoutDataWidget.DelegatingDataWidgetFactory(
				cell, cellClass);
		return new SliderDataPanel<JsCatalogEntry>(factory, null,
				viewportWidth, viewportHeight, cellWrapperClass);

	}

	
	public void setViewportWidth(String s){
		this.viewportWidth=Integer.parseInt(s);
	}
	public void setViewportHeight(String s){
		this.viewportHeight=Integer.parseInt(s);
	}
	public void setCellWrapperClass(String s){
		this.cellWrapperClass=s;
	}
	public void setCellClass(String s){
		this.cellClass=s;
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
	public void init(FieldDescriptor field, JavaScriptObject fieldProperties, JsTransactionActivityContext contextParameters,
			ProcessContextServices contextServices, RelationshipDelegate delegate, GenericDataProvider dataProvider, CatalogAction mode) {
		
	}
}
