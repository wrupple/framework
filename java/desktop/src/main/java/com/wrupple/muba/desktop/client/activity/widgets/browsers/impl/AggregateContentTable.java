package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class AggregateContentTable extends AbstractContentBrowser {

	TableLayoutDelegate tableInitializer;
	String[] customColumnIds;
	@Inject
	public AggregateContentTable(TableLayoutDelegate tableInitializer,
			Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProvider, Resources tableResources, CatalogEntryBrowserMap browserMap) {
		super(new AggregateDataTable<JsCatalogEntry>(tableResources), dinamicDataProviderProvider, keyProvider, null, browserMap);
		pagingEnabled=false;
		this.tableInitializer=tableInitializer;
	}

	@Override
	public void setRuntimeParams(String catalog, JavaScriptObject properties,
			EventBus bus, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices) {
		setCumulative("true");
		
		
		// initialize columns
		

		tableInitializer.initialize(customColumnIds, getUnderlyingTable(), bus,
				contextServices, contextParameters, properties);
		super.setRuntimeParams(catalog, properties, bus, contextParameters, contextServices);
	}
	
	public void setCustomColumnIds(String rawcustomColumnIds){
		if(rawcustomColumnIds==null){
			customColumnIds=null;
		}else{
			customColumnIds = rawcustomColumnIds.split(",");
		}
	}
	
	public void setGroupingField(String groupingField) {
		AggregateDataTable<JsCatalogEntry> underlying = getUnderlyingTable();
		underlying.setGroupingField(groupingField);
	}
	
	public AggregateDataTable<JsCatalogEntry> getUnderlyingTable() {
		return (AggregateDataTable<JsCatalogEntry>) super.hasData;
	}
	
	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		// TODO i dont think is is viable? unless we redraw the entire thing?

	}

}
