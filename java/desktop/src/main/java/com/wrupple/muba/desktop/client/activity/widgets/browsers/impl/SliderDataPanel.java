package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.wrupple.muba.desktop.client.activity.widgets.CollapsibleSimplePanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.BackAndForthPager;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegateImpl;
import com.wrupple.muba.desktop.client.services.presentation.layout.ValueDependableStyleDelegate;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.CarouselCellPositioner;

/**
 * 
 * 
 * @author japi
 *
 * @param <T>
 */
public class SliderDataPanel<T> extends Composite implements HasData<T> {
	
	HorizontalPanel main;
	LayoutDataPanel<T> dataPanel;
	
	public SliderDataPanel(
			com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.LayoutDataPanel.DataWidgetFactory<T> factory,
			ValueDependableStyleDelegate styleDelegate, int viewportWidth, int viewportHeight, String cellWrapperClass) {
		main = new HorizontalPanel();
		main.addStyleName(BackAndForthPager.STYLE_NAME);
		
		IndexedLayoutDelegateImpl layoutDelegate = new IndexedLayoutDelegateImpl(
				new CarouselCellPositioner(viewportWidth, viewportHeight));
		layoutDelegate.setCellWrapperClass(cellWrapperClass);
		dataPanel = new LayoutDataPanel<T>(factory, layoutDelegate, styleDelegate);
		dataPanel.setSize(viewportWidth+"px", viewportHeight+"px");
		dataPanel.setOverflow("hidden");
		Label backButton = new Label("<");
		backButton.addStyleName(CollapsibleSimplePanel.DIRECTIONAL_CONTROL_STYLE);
		backButton.addStyleName("backButton");
		Label forwardButton = new Label(">");
		forwardButton.addStyleName("forwardButton");
		forwardButton.addStyleName(CollapsibleSimplePanel.DIRECTIONAL_CONTROL_STYLE);
		//TODO make LTR sensitive
		main.add(backButton);
		main.add(dataPanel);
		main.add(forwardButton);
		
		// Handle scroll events.
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				decrement();
			}
		});
		forwardButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increment();
			}
		});
		
		initWidget(main);
	}

	protected void increment() {
		int currentFocus = dataPanel.getFocusIndex();
		dataPanel.setFocusIndex(currentFocus+1);
	}

	protected void decrement() {
		int currentFocus = dataPanel.getFocusIndex();
		if(currentFocus==0){
			//ignore obnoxious user
		}else{
			dataPanel.setFocusIndex(currentFocus-1);
			//redraw
			dataPanel.setRowData(0, dataPanel.getVisibleItems());
		}
	}


	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		return dataPanel.addRangeChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return dataPanel.addRowCountChangeHandler(handler);
	}

	@Override
	public int getRowCount() {
		return dataPanel.getRowCount();
	}

	@Override
	public Range getVisibleRange() {
		return dataPanel.getVisibleRange();
	}

	@Override
	public boolean isRowCountExact() {
		return dataPanel.isRowCountExact();
	}

	@Override
	public void setRowCount(int count) {
		dataPanel.setRowCount(count);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		dataPanel.setRowCount(count, isExact);
	}

	@Override
	public void setVisibleRange(int start, int length) {
		dataPanel.setVisibleRange(start, length);
	}

	@Override
	public void setVisibleRange(Range range) {
		dataPanel.setVisibleRange(range);
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(
			com.google.gwt.view.client.CellPreviewEvent.Handler<T> handler) {
		return dataPanel.addCellPreviewHandler(handler);
	}

	@Override
	public SelectionModel<? super T> getSelectionModel() {
		return dataPanel.getSelectionModel();
	}

	@Override
	public T getVisibleItem(int indexOnPage) {
		return dataPanel.getVisibleItem(indexOnPage);
	}

	@Override
	public int getVisibleItemCount() {
		return dataPanel.getVisibleItemCount();
	}

	@Override
	public Iterable<T> getVisibleItems() {
		return dataPanel.getVisibleItems();
	}

	@Override
	public void setRowData(int start, List<? extends T> values) {
		dataPanel.setRowData(start, values);
	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		dataPanel.setSelectionModel(selectionModel);
	}

	@Override
	public void setVisibleRangeAndClearData(Range range,
			boolean forceRangeChangeEvent) {
		dataPanel.setVisibleRangeAndClearData(range, forceRangeChangeEvent);
	}
}
