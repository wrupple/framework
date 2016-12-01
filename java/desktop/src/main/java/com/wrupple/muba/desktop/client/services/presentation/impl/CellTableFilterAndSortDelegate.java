package com.wrupple.muba.desktop.client.services.presentation.impl;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.widgets.fields.column.FieldColumn;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterDataOrdering;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.FilterDataOrdering;

public class CellTableFilterAndSortDelegate implements
		ValueChangeHandler<FilterData> {

	class SortHandler extends AsyncHandler {

		public SortHandler(HasData<?> arg0) {
			super(arg0);
		}

		@Override
		public void onColumnSort(ColumnSortEvent event) {
			columns = event.getColumnSortList();

			pushChangesToview(lastValue);

			super.onColumnSort(event);
		}

	}
	
	AbstractCellTable<JsCatalogEntry> display;
	private FilterableDataProvider<JsCatalogEntry> dataProvider;
	
	private FilterData lastValue;
	private ColumnSortList columns;
	
	@Inject
	public CellTableFilterAndSortDelegate() {
		super();
	}

	@Override
	public void onValueChange(ValueChangeEvent<FilterData> event) {
		FilterData filter = event.getValue();
		pushChangesToview(filter);
		display.setVisibleRangeAndClearData(display.getVisibleRange(), true);
	}

	public void pushChangesToview(FilterData filter) {
		ArrayList<FilterDataOrdering> order;
		if (columns == null) {
			order = null;
		}else{
			ColumnSortInfo columnInfo;
			Column<?, ?> column;
			FieldColumn catalogColumn;
			order = new ArrayList<FilterDataOrdering>(
					columns.size());
			FilterDataOrdering columnOrder;
			for (int i = 0; i < columns.size(); i++) {
				columnInfo = columns.get(0);
				column = columnInfo.getColumn();
				try {
					catalogColumn = (FieldColumn) column;
					columnOrder = JsFilterDataOrdering.newFilterDataOrdering();
					columnOrder.setAscending(columnInfo.isAscending());
					columnOrder.setField(catalogColumn.getFieldId());
					order.add(columnOrder);
				} catch (Exception e) {
					GWT.log("I tried, i did! But i dont know how to sort this type of column",
							e);
				}
			}
		}
		filter.setOrdering(order);
		this.lastValue = filter;
	}

	public void setDataProviderFilters(FilterData filter) {
		this.dataProvider.setFilter(filter);
	}

	public void initialize (AbstractCellTable<JsCatalogEntry> display,FilterableDataProvider<JsCatalogEntry> dataProvider){
		this.dataProvider=dataProvider;
		this.display=display;
		
		display.addColumnSortHandler(new SortHandler(display));
	}
}
