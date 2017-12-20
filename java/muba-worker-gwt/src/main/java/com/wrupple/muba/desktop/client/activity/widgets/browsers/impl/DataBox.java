package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.List;

public class DataBox extends ListBox implements HasData<JsCatalogEntry> {

	private  ArrayList<JsCatalogEntry> visible;
	private Range range;
	private SelectionModel<? super JsCatalogEntry> selectionModel;
	private JsCatalogEntry jsCatalogEntry;
	
	public DataBox() {
		super();
		visible = new ArrayList<JsCatalogEntry>();
		range = new Range(0, FilterData.DEFAULT_INCREMENT);
		super.addItem("...");
	}
	
	@Override
	public void setRowData(int start, List<? extends JsCatalogEntry> values) {
		for(int i = 0 ; i< values.size(); i++){
			if(i<visible.size()){
				visible.set(i+start, values.get(i));
			}else{
				visible.add(i, values.get(i));
			}
			
		}
		render();
	}
	
	private void render() {
		int index= getItemCount()-1;
		JsCatalogEntry e;
		String name;
		for(int i = index;i<visible.size(); i++){
			e = visible.get(i);
			name = e.getName();
			if(name==null){
				name = e.getId();
			}
			addItem(name);
			if(this.jsCatalogEntry!=null){
				if(this.jsCatalogEntry.getId().equals(e.getId())){
					setSelectedIndex(i+1);
				}
			}
		}
	}

	public void setSelection(JsCatalogEntry jsCatalogEntry) {
		int index = visible.indexOf(jsCatalogEntry);
		if(index>=0){
			if(selectionModel!=null){
				selectionModel.setSelected(jsCatalogEntry, true);
			}
			if(getSelectedIndex()!=index+1){
				setSelectedIndex(index+1);
			}
		}
		this.jsCatalogEntry=jsCatalogEntry;
	}

	public JsCatalogEntry getSelectedEntry() {
		int index = getSelectedIndex()-1;
		if(index>=0){
			return visible.get(index);
			
		}else{
			return jsCatalogEntry;
		}
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		return addHandler(handler, RangeChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return addHandler(handler, RowCountChangeEvent.getType());
	}

	@Override
	public int getRowCount() {
		return visible.size();
	}

	@Override
	public Range getVisibleRange() {
		return range;
	}

	@Override
	public boolean isRowCountExact() {
		return true;
	}

	@Override
	public void setRowCount(int count) {
		if(count<getRowCount()){
			visible  = new ArrayList<JsCatalogEntry>(visible.subList(0, count-1));
			RowCountChangeEvent.fire(this, getRowCount(), true);
		}
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		setRowCount(count);
	}

	@Override
	public void setVisibleRange(int start, int length) {
		setVisibleRange(new Range(start,length));
	}

	@Override
	public void setVisibleRange(Range range) {
		this.range=range;
	}
	
	@Override
	public JsCatalogEntry getVisibleItem(int indexOnPage) {
		return visible.get(indexOnPage);
	}

	@Override
	public Iterable<JsCatalogEntry> getVisibleItems() {
		return visible;
	}

	@Override
	public void setVisibleRangeAndClearData(Range range, boolean forceRangeChangeEvent) {
		setVisibleRange(range);
		if(forceRangeChangeEvent){
			RangeChangeEvent.fire(this, range);
		}
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(com.google.gwt.view.client.CellPreviewEvent.Handler<JsCatalogEntry> handler) {
		return addHandler(handler, CellPreviewEvent.getType());
	}
	

	@Override
	public void setSelectionModel(SelectionModel<? super JsCatalogEntry> selectionModel) {
		this.selectionModel=selectionModel;
	}

	@Override
	public SelectionModel<? super JsCatalogEntry> getSelectionModel() {
		return selectionModel;
	}

	

}
