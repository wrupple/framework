package com.wrupple.muba.desktop.client.services.presentation;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateDataTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.IndexedContentTable;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;


public interface TableLayoutDelegate {

	void  initialize(String host,String domain,CatalogDescriptor catalog,Collection<FieldDescriptor> columns,JsArray<JsCatalogEntry> selections,AbstractCellTable<JsCatalogEntry> display, EventBus bus, ProcessContextServices contextServices, JsTransactionActivityContext contextParameters, JavaScriptObject browserDescriptor, IndexedContentTable table);

	void initialize(String[] customColumnIds,
			AggregateDataTable<JsCatalogEntry> underlyingTable, EventBus bus,
			ProcessContextServices services, JsTransactionActivityContext ctxt,
			JavaScriptObject properties);
	
}
