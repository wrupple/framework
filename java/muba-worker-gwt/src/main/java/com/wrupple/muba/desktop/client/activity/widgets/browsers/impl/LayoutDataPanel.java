package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.wrupple.muba.desktop.client.activity.widgets.panels.PanelWithLayoutPositioner;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.layout.ValueDependableStyleDelegate;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayoutDataPanel<T> extends PanelWithLayoutPositioner implements HasData<T> {

	public interface DataWidgetFactory<T> {

		IsWidget getWidget(T element);

		void updatevalue(int index, T value);

        void setCellClass(String cellClass);

	}

	private class AddToSelection implements ClickHandler {

		private T object;
		private final int absoluteIndex;

		public AddToSelection(int absoluteIndex, T object) {
			super();
			this.object = object;
			this.absoluteIndex = absoluteIndex;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (selectionModel != null) {
				boolean selected = !selectionModel.isSelected(object);
				selectionModel.setSelected(object, selected);
				setFocusIndex(absoluteIndex);
			}
		}

	}

	private List<HandlerRegistration> handlerRegistry;
	private DataWidgetFactory<T> factory;
	private SelectionModel<? super T> selectionModel;
	private ValueDependableStyleDelegate styleDelegate;
	private int focusIndex;

	/*
	 * model
	 */
	// loaded visible items
	private ArrayList<T> visibleData;
	// even if data is not loaded, how large should the range be
	private int rangeLength = FilterData.DEFAULT_INCREMENT;
	private int deferredFocusIndex = -1;

	public LayoutDataPanel(DataWidgetFactory<T> factory, IndexedLayoutDelegate indexedLayoutDelegate, ValueDependableStyleDelegate styleDelegate) {
		super(indexedLayoutDelegate);
		this.factory = factory;
		this.styleDelegate = styleDelegate;
		handlerRegistry = new ArrayList<HandlerRegistration>();
		visibleData = new ArrayList<T>();
		focusIndex = 0;
		setOverflow("auto");
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		Range visibleRange = getVisibleRange();
		setRowData(visibleRange.getStart(), getVisibleItems());
	}

	@Override
	public void onResize() {
		super.onResize();
		Range visibleRange = getVisibleRange();
		setRowData(visibleRange.getStart(), getVisibleItems());
	}

	@Override
	public void setRowData(int start, List<? extends T> values) {
		// remove previous iteration click handlers
		for (HandlerRegistration reg : handlerRegistry) {
			reg.removeHandler();
		}
		handlerRegistry.clear();

		forceLayout();
		// TODO this fails if row date is received before the widget is
		// attached. shoudl recover
		int height = isAttached() ? (int) (GWTUtils.getNonZeroParentHeight(this) * zoomFactor) : 100;
		int width = isAttached() ? (int) (GWTUtils.getNonZeroParentWidth(this) * zoomFactor) : 100;
		Range visibleRange = getVisibleRange();
		int lastItemIndex = visibleRange.getStart() + visibleRange.getLength();
		if (focusIndex > lastItemIndex) {
			setFocusIndex(lastItemIndex);
		}

		int previousVisibleDataSize = visibleData.size();
		updateModel(start, values);
		if (deferredFocusIndex > -1 && previousVisibleDataSize < visibleData.size()) {
			// a focus index change was deferred, and visible item size has
			// increased
			this.focusIndex = deferredFocusIndex;
			deferredFocusIndex = -1;
		}

		indexedLayoutDelegate.initializePositions(visibleData, width, height, focusIndex);
		// this in turn calls render on this object
		forceLayout();

		updateWidget(start, values);
		animate(500);
	}

	private void updateWidget(int start, List<? extends T> values) {
		T value;
		IsWidget w;
		Element container;
		HandlerRegistration handlerRegistration;
		for (int i = start; i < values.size(); i++) {
			value = values.get(i);

			w = factory.getWidget(value);
			container = positionElement(w, i);
			// TODO this system of adding handlers on each redraw is super
			// wasteful, specially when animating
			handlerRegistration = w.asWidget().addHandler(new AddToSelection(i, value), ClickEvent.getType());
			handlerRegistry.add(handlerRegistration);

			if (styleDelegate != null && value != null) {
				styleDelegate.applyValueStyle(container, value);
			}
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

	public void setFocusIndex(final int newfocusIndex) {
		if (newfocusIndex < 0) {
			return;
		}
		int lastItemIndex = visibleData.size() - 1;
		if (newfocusIndex > lastItemIndex) {
			if (rangeLength == Integer.MAX_VALUE) {
				// cannot increase
			} else {
				// increase range, defer focus index increase if more results
				// are found
				setVisibleRange(0, lastItemIndex + FilterData.DEFAULT_INCREMENT);
				this.deferredFocusIndex = newfocusIndex;
			}
		} else if (newfocusIndex <= lastItemIndex) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					// set new focus index redraw
					focusIndex = newfocusIndex;
					// redraw
					setRowData(0, getVisibleItems());
				}
			});

		}

	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		this.selectionModel = selectionModel;
	}

	@Override
	public SelectionModel<? super T> getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void setVisibleRangeAndClearData(Range range, boolean forceRangeChangeEvent) {
		setVisibleRange(range.getStart(), range.getLength());
		if (forceRangeChangeEvent) {
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
		return new Range(0, rangeLength);
	}

	@Override
	public void setVisibleRange(int start, int length) {

		assert start == 0 : "This data widget only supports starting from 0";
		int visibleDataSize = visibleData.size();
		if (visibleDataSize == length) {
			// nothing to do
		} else {
			if (length > visibleDataSize) {
				// we need more data!
				rangeLength = length;
			} else {
				// trim data
				trim(length + 1, visibleDataSize - 1);
				visibleData = (ArrayList<T>) visibleData.subList(0, length);
				rangeLength = visibleData.size();
			}
			fireRangeChangeEvent(new Range(start, length));
		}

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

	public void upateValue(int visibleIndex, JavaScriptObject receivedUpdate) {
		factory.updatevalue(visibleIndex, (T) receivedUpdate);
	}

}
