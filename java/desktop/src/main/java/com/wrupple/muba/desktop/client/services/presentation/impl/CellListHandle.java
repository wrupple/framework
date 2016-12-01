package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.AbstractForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.client.services.presentation.ForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class CellListHandle extends AbstractForeignRelationWidgetHandle implements ForeignRelationWidgetHandle {

	@Override
	public HasData<JsCatalogEntry> get(Cell<JsCatalogEntry> cell) {
		return new CellList<JsCatalogEntry>(cell);
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
