package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public interface ForeignRelationWidgetHandle {
	
	HasData<JsCatalogEntry> get(Cell<JsCatalogEntry> cell);

    int getPageSize();

    void setPageSize(String s);

    String getCustomJoins();

    boolean showAddRelation();
	
	boolean showRemoveSelectionFromRelation();

    void setCustomJoins(String s);

	void init(FieldDescriptor field, JavaScriptObject fieldProperties, JsTransactionApplicationContext contextParameters, ProcessContextServices contextServices,
              RelationshipDelegate delegate, GenericDataProvider dataProvider, CatalogAction mode);

}
