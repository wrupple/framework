package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sgx.raphael4gwt.raphael.widget.PaperWidget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils.DataGroup;
import com.wrupple.vegetate.domain.FilterData;

public class AggregateDataCanvas<T extends JavaScriptObject> extends PaperWidget implements HasData<T>,RequiresResize{
	
	public interface AggregateRenderService<T  extends JavaScriptObject> {
		void renderGroup(AggregateDataCanvas<T> canvas,List<T> data,final JsArray<T> members, final String value);

		void startDrawing(AggregateDataCanvas<T> canvas);
	}
	
	private AggregateRenderService<T> cell;
	private List<T> visibleData;
	//even if data is not loaded, how large should the range be
	private int rangeLength=FilterData.DEFAULT_INCREMENT;
	String groupingField;
		
	public AggregateDataCanvas(AggregateRenderService<T> cell,int width, int height) {
		super(width, height);
		visibleData = new ArrayList<T>();
		this.cell=cell;
	}
	
	/*
	 * DRAWING
	 * 
	 */


	@Override
	public void setRowData(int start, List<? extends T> values) {
		assert start==0 : "DataCanvas can only draw items starting from the first row";
		this.visibleData=(List<T>)values;
		cell.startDrawing(this);
		
		 Collection<DataGroup<T>> groups = GWTUtils.groupData(visibleData, groupingField);

		for (DataGroup<T> group : groups) {
			drawGroup( group);
		}
	}
	
	/*
	 * SELECTION 
	 * 
	 */
	
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
		assert start==0:"This data widget only supports starting from 0";
		int visibleDataSize = visibleData.size();
		if(visibleDataSize==length){
			//nothing to do
		}else{
			if(length>visibleDataSize){
				//we need more data!
				
			}else{
				//trim data
				visibleData=(ArrayList<T>) visibleData.subList(0, length);
				setRowData(0, visibleData);
			}
			rangeLength = length;
			RangeChangeEvent.fire(this, new Range(start,length));
		}
		
	}


	public AggregateRenderService<T> getCell() {
		return cell;
	}

	public void setCell(AggregateRenderService<T> cell) {
		this.cell = cell;
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
	public void setVisibleRangeAndClearData(Range range,boolean forceRangeChangeEvent) {
		setVisibleRange(range);
		if(forceRangeChangeEvent){
			RangeChangeEvent.fire(this, range);
		}
	}
	
	@Override
	public Range getVisibleRange() {
		return new Range(0,rangeLength);
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
	public void onResize() {
		
	}

	@Override
	public boolean isRowCountExact() {
		return true;
	}

	private void drawGroup( DataGroup<T> group) {
		JsArray<T> members = group.getMembers();
		String value = group.getValue();
		cell.renderGroup(this, visibleData,members, value);
		
	}

	public String getGroupingField() {
		return groupingField;
	}

	public void setGroupingField(String groupingField) {
		this.groupingField = groupingField;
	}
	
}
