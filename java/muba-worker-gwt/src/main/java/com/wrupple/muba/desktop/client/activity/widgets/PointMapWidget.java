package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.events.MouseEvent;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.overlays.InfoWindow;
import com.google.gwt.maps.client.overlays.InfoWindowOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointMapWidget<T> extends ResizeComposite implements HasData<T> {
	
	private class AddToSelection implements ClickMapHandler {

		private T object;
		private int absoluteIndex;

		public AddToSelection(int absoluteIndex, T object) {
			super();
			this.object = object;
			this.absoluteIndex=absoluteIndex;
		}


		@Override
		public void onEvent(ClickMapEvent event) {
			if(selectionModel!=null){
				boolean selected = !selectionModel.isSelected(object);
				selectionModel.setSelected(object,selected );
				setFocusIndex(absoluteIndex);
			}
		}
		
	}
	
	public interface MarkerFactory<T> {
		
		Marker getWidget(T entry);

		void setMapWidget(MapWidget mapWidget);
		
	}
	
	/*
	 * model
	 */
	//loaded visible items
	private ArrayList<T> visibleData;
	//even if data is not loaded, how large should the range be
	private int rangeLength=FilterData.DEFAULT_INCREMENT;
	private SelectionModel<? super T> selectionModel;
	private int focusIndex;
	
	private final MapWidget mapWidget;
	private MarkerFactory<T> factory;
	
	public PointMapWidget(MapOptions opts, MarkerFactory<T> factory) {
		visibleData = new ArrayList<T>();
		focusIndex=0;
		mapWidget = new MapWidget(opts);
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		container.setWidget(mapWidget);
		initWidget(container);
		this.factory=factory;
		factory.setMapWidget(mapWidget);
	}

	@Override
	public void setRowData(int start, List<? extends T> values) {
		
		Range visibleRange = getVisibleRange();
		int lastItemIndex = visibleRange.getStart()+visibleRange.getLength();
		if(focusIndex>lastItemIndex){
			setFocusIndex(lastItemIndex);
		}
		
		updateModel(start,values);
		
		updateWidget(start, values);

	}
	
	private void updateWidget(int start, List<? extends T> values){
		T element;
		Marker w;
		for(int i = start ; i< values.size(); i++){
			element = values.get(i);
			
			w = factory.getWidget(element);
			
			w.addClickHandler(new AddToSelection(i,element));
			
		}
	}

	
	
	private void updateModel(int start, List<? extends T> values) {
		int indexToAddInto;
		T element;
		for (int i = 0; i < values.size(); i++) {
			indexToAddInto = i + start;
			element = values.get(i);
			if (indexToAddInto == visibleData.size()) {
				visibleData.add(element);
			} else {
				visibleData.set(indexToAddInto, element);
			}
		}
	}


	public int getFocusIndex() {
		return focusIndex;
	}

	public void setFocusIndex(int focusIndex) {
		this.focusIndex = focusIndex;
	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		this.selectionModel=selectionModel;
	}
	
	@Override
	public SelectionModel<? super T> getSelectionModel() {
		return selectionModel;
	}
	
	
	@Override
	public void setVisibleRangeAndClearData(Range range, boolean forceRangeChangeEvent) {
		setVisibleRange(range.getStart(),range.getLength());
		if(forceRangeChangeEvent){
			fireRangeChangeEvent(range);
		}
	}

	@Override
	public void setRowCount(int count) {
		setRowCount(count, true);
	}
	
	@Override
	public int getRowCount() {
		return visibleData.size();
	}
	@Override
	public boolean isRowCountExact() {
		return true;
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		assert isExact : "This widget only supports exact row counts";
		if (count == visibleData.size()) {
			// we are ok!
		} else {
			setVisibleRange(0, count);
		}
	}
	
	@Override
	public HandlerRegistration addCellPreviewHandler(com.google.gwt.view.client.CellPreviewEvent.Handler<T> handler) {
		return addHandler(handler, CellPreviewEvent.getType());
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
	public Range getVisibleRange() {
		return new Range(0,  rangeLength);
	}


	@Override
	public void setVisibleRange(int start, int length) {
		
		assert start==0:"This data widget only supports starting from 0";
		int visibleDataSize = visibleData.size();
		if(visibleDataSize==length){
			//nothing to do
		}else{
			if(length>visibleDataSize){
				//we need more data!
				rangeLength = length;
			}else{
				//trim data
				trim(length+1,visibleDataSize-1);
				visibleData=(ArrayList<T>) visibleData.subList(0, length);
				rangeLength = visibleData.size();
			}
			fireRangeChangeEvent(new Range(start,length));
		}
		
	}
	
	private void trim(int fromIndex, int toIndex) {
		for(; fromIndex<=toIndex ; fromIndex++){
			//TODO panel.remove(fromIndex);
			//TODO remove markers
		}
	}
	
	 private void drawInfoWindow(Marker marker, MouseEvent mouseEvent) {
		    if (marker == null || mouseEvent == null) {
		      return;
		    }

		    HTML html = new HTML("You clicked on: " + mouseEvent.getLatLng().getToString());

		    InfoWindowOptions options = InfoWindowOptions.newInstance();
		    options.setContent(html);
		    InfoWindow iw = InfoWindow.newInstance(options);
		    iw.open(mapWidget, marker);
		  }

	@Override
	public void setVisibleRange(Range range) {
		setVisibleRange(range.getStart(), range.getLength());
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
	private void fireRangeChangeEvent(Range range) {
		RangeChangeEvent.fire(this, range);
	}

	public MapWidget getMapWidget() {
		return mapWidget;
	}
	
	
}
