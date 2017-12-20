package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils.DataGroup;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * AggregateDataCanvas and this shoudl have an ancestor class, it is quite
 * obvious
 * 
 * @author japi
 * 
 * @param <T>
 */
public class AggregateDataTable<T extends JavaScriptObject> extends Composite
		implements HasData<T>,RequiresResize {

	static class GroupKeyProvider<T extends JavaScriptObject> implements
			ProvidesKey<DataGroup<T>> {

		@Override
		public Object getKey(DataGroup<T> item) {
			return item.getValue();
		}

	}

	private final CellTable<DataGroup<T>> wrapped;
	private final ListDataProvider<DataGroup<T>> dataProvider;
	private  List<T> visibleData;
	// even if data is not loaded, how large should the range be
	private int rangeLength = FilterData.DEFAULT_INCREMENT;
	private String groupingField;

	public AggregateDataTable(Resources resources) {
		super();
		visibleData = new ArrayList<T>();
		GroupKeyProvider<T> keyProvider = new GroupKeyProvider<T>();
		dataProvider = new ListDataProvider<GWTUtils.DataGroup<T>>(keyProvider);
		wrapped = new CellTable<GWTUtils.DataGroup<T>>(Integer.MAX_VALUE,resources, keyProvider);
		wrapped.setWidth("100%");
		dataProvider.addDataDisplay(wrapped);
		initWidget(wrapped);
	}

	public void addColumn(Column<DataGroup<T>, ?> column, String header) {
		if(header==null){
			wrapped.addColumn(column);
		}else{
			wrapped.addColumn(column, header);
		}
	}

	@Override
	public void setRowData(int start, List<? extends T> values) {
		assert start == 0 : "can only draw items starting from the first row";
		this.visibleData = (List<T>) values;

		Collection<DataGroup<T>> groups = GWTUtils.groupData(visibleData,
				groupingField);
		
		dataProvider.setList(new ArrayList<DataGroup<T>>(groups));
	}

	@Override
	public SelectionModel<? super T> getSelectionModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		// TODO Auto-generated method stub

	}

	/*
	 * GARBAGE METHODS
	 */

	@Override
	public void setVisibleRange(int start, int length) {
		assert start == 0 : "This data widget only supports starting from 0";
		int visibleDataSize = visibleData.size();
		if (visibleDataSize == length) {
			// nothing to do
		} else {
			if (length > visibleDataSize) {
				// we need more data!

			} else {
				// trim data
				setRowData(0, visibleData.subList(0, length));
			}
			rangeLength = length;
			RangeChangeEvent.fire(this, new Range(start, length));
		}

	}

	@Override
	public void setRowCount(int count) {
		setRowCount(count, true);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		assert isExact : "DataCanvas row count can only be exact";
		setVisibleRange(0, count);
	}

	@Override
	public void setVisibleRange(Range range) {
		setVisibleRange(range.getStart(), range.getLength());
	}

	@Override
	public void setVisibleRangeAndClearData(Range range,
			boolean forceRangeChangeEvent) {
		setVisibleRange(range);
		if (forceRangeChangeEvent) {
			RangeChangeEvent.fire(this, range);
		}
	}

	@Override
	public Range getVisibleRange() {
		return new Range(0, rangeLength);
	}

	@Override
	public T getVisibleItem(int indexOnPage) {
		return visibleData.get(indexOnPage);
	}

	@Override
	public int getVisibleItemCount() {
		return visibleData.size();
	}

	@Override
	public List<T> getVisibleItems() {
		return Collections.unmodifiableList(visibleData);
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(
			com.google.gwt.view.client.CellPreviewEvent.Handler<T> handler) {
		return addHandler(handler, CellPreviewEvent.getType());
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		return addHandler(handler, RangeChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return addHandler(handler, RowCountChangeEvent.getType());
	}

	@Override
	public int getRowCount() {
		return visibleData.size();
	}

	@Override
	public boolean isRowCountExact() {
		return true;
	}

	public String getGroupingField() {
		return groupingField;
	}

	public void setGroupingField(String groupingField) {
		this.groupingField = groupingField;
	}

	public CellTable<DataGroup<T>> getWrappedTable() {
		return wrapped;
	}

	@Override
	public void onResize() {
		// nothing to do?
		
	}

}
