package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.CellTableFilterAndSortDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

public class IndexedContentTable extends AbstractContentBrowser implements ContentBrowser {

	class InitializeTableBrowser extends DataCallback<CatalogDescriptor> {

		private AbstractCellTable<JsCatalogEntry> display;
		private JavaScriptObject properties;
		private JsArray<JsCatalogEntry> selections;
		private EventBus bus;
		private ProcessContextServices contextServices;
		private JsTransactionActivityContext contextParameters;
		private IndexedContentTable table;

		public InitializeTableBrowser(EventBus bus, ProcessContextServices contextServices, JsTransactionActivityContext contextParameters,
				AbstractCellTable<JsCatalogEntry> display, JavaScriptObject properties, JsArray<JsCatalogEntry> selections, IndexedContentTable table) {
			super();
			this.bus = bus;
			this.contextServices = contextServices;
			this.selections = selections;
			this.display = display;
			this.properties = properties;
			this.contextParameters = contextParameters;
			this.table = table;
		}

		@Override
		public void execute() {
			Collection<FieldDescriptor> summaryFields = fieldDescriptor.getSummaryDescriptors(result).values();

			tableInitializer.initialize(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), result, summaryFields, selections, display, bus, contextServices, contextParameters, properties, table);
		}

	}

	private class MyDataGrid<T> extends DataGrid {
		public ScrollPanel getScrollPanel() {
			HeaderPanel header = (HeaderPanel) getWidget();
			return (ScrollPanel) header.getContentWidget();
		}
	}

	private TableLayoutDelegate tableInitializer;
	private FieldDescriptionService fieldDescriptor;
	private String addSelectorColumn;

	@Inject
	public IndexedContentTable( FieldDescriptionService fieldDescriptor, TableLayoutDelegate tableInitializer,
			Provider<GenericDataProvider> dinamicDataProviderProvider, CatalogEntryKeyProvider keyProvider, CatalogEntryBrowserMap browserMap) {
		super(new DataGrid<JsCatalogEntry>(FilterData.DEFAULT_INCREMENT, keyProvider), dinamicDataProviderProvider, keyProvider, null, browserMap);
		((AbstractCellTable) super.hasData).setWidth("100%");
		this.tableInitializer = tableInitializer;
		this.fieldDescriptor = fieldDescriptor;
	}

	public AbstractCellTable<JsCatalogEntry> getUnderlyingTable() {
		return (AbstractCellTable<JsCatalogEntry>) super.hasData;
	}

	@Override
	public void setRuntimeParams(String catalog, JavaScriptObject properties, EventBus bus, JsTransactionActivityContext contextParameters,
			ProcessContextServices contextServices) {

		AbstractCellTable<JsCatalogEntry> cellTable = getUnderlyingTable();

		// initialize columns
		JsArray<JsCatalogEntry> selections = null;
		if (addSelectorColumn != null) {
			JavaScriptObject jso = GWTUtils.getAttributeAsJavaScriptObject(contextParameters, addSelectorColumn);
			if (jso != null) {
				selections = jso.cast();
			}
		}

		
		contextServices.getStorageManager().loadCatalogDescriptor(contextServices.getDesktopManager().getCurrentActivityHost(),contextServices.getDesktopManager().getCurrentActivityDomain(), catalog, new InitializeTableBrowser(bus, contextServices, contextParameters, cellTable, properties, selections,
				this));

		super.setRuntimeParams(catalog, properties, bus, contextParameters, contextServices);
	}

	public void setAddSelectorColumn(String addSelectorColumn) {
		this.addSelectorColumn = addSelectorColumn;
	}

	@Override
	public void setValue(JsArray<JsCatalogEntry> value) {
		super.setValue(value);
		if(!GWTUtils.isArray(value)){
			AbstractCellTable<JsCatalogEntry> table = getUnderlyingTable();
			CellTableFilterAndSortDelegate filterSortDelegate = new CellTableFilterAndSortDelegate();
			filterSortDelegate.initialize(table, (FilterableDataProvider<JsCatalogEntry>) getDataProvider());
		}
	}

	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		getUnderlyingTable().setRowData(visibleIndex, (List) Collections.singleton(receivedUpdate));
	}


}
