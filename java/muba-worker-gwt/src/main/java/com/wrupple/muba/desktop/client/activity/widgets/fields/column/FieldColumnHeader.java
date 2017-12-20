package com.wrupple.muba.desktop.client.activity.widgets.fields.column;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.FieldColumnHeaderCell;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.TableHeaderData;

public class FieldColumnHeader extends Header<TableHeaderData> implements HasValue<TableHeaderData>, ValueChangeHandler<JsFilterData> {

	private final FieldColumnHeaderCell delegate;
	private final JsFieldDescriptor field;

	public FieldColumnHeader(FilterCriteriaFieldDelegate delega, JsFieldDescriptor field) {
		super(new FieldColumnHeaderCell(delega, field));
		this.field = field;
		this.delegate = (FieldColumnHeaderCell) super.getCell();
		TableHeaderData value = TableHeaderData.createObject().cast();
		value.setCriteria(null);
		value.setDescriptor(field);
		setValue(value, false);
	}

	private HandlerManager handlerManager;
	private TableHeaderData value;

	@Override
	public TableHeaderData getValue() {
		return value;
	}

	@Override
	public void setValue(TableHeaderData value) {
		this.value = value;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TableHeaderData> handler) {
		return ensureHandlerManager().addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		ensureHandlerManager().fireEvent(event);
	}

	@Override
	public void setValue(TableHeaderData value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	private HandlerManager ensureHandlerManager() {
		if (handlerManager == null) {
			handlerManager = new HandlerManager(this);
		}
		return handlerManager;
	}

	public void onBrowserEvent(Context context, Element elem, NativeEvent nativeEvent) {
		int eventType = Event.as(nativeEvent).getTypeInt();
		GWT.log("header event" + Event.as(nativeEvent).getString());
		if (eventType == Event.ONCHANGE) {
			GWT.log("[content table filter]change event "+field.getFieldId());
			nativeEvent.preventDefault();
			// use value setter to easily fire change event to handlers
			setValue(delegate.getCurrentInputValue(elem), true);
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<JsFilterData> event) {
		TableHeaderData value = TableHeaderData.createObject().cast();
		JsFilterData filter = event.getValue();
		if (filter == null) {
			value.setCriteria(null);
		} else {
            value.setCriteria(filter.fetchCriteria(field.getFieldId()));
        }
		value.setDescriptor(field);
		setValue(value,false);
	}

}
