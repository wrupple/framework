package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.Set;

public class JSOAdapterCell<C> implements Cell<JavaScriptObject> {

	private static class ValueUpdateAdapter<C> implements ValueUpdater<C> {

		private ValueUpdater<JavaScriptObject> wrapped;
		private JSOAdapterCell<C> parent;

		public ValueUpdateAdapter(ValueUpdater<JavaScriptObject> wrapped, JSOAdapterCell<C> cell) {
			super();
			this.wrapped = wrapped;
			this.parent = cell;
		}

		@Override
		public void update(C value) {
			wrapped.update(parent.toJSO(value));
		}

	}

	public interface JSOAdapter<C> {
		C fromJSO(Object value);

		JavaScriptObject toJSO(C value);
	}

	private Cell<C> wrapped;
	private JSOAdapter<C> adapter;

	public JSOAdapterCell(Cell<C> wrapped, JSOAdapter<C> adapter) {
		super();
		this.wrapped = wrapped;
		this.adapter = adapter;
	}

	@Override
	public boolean dependsOnSelection() {
		return wrapped.dependsOnSelection();
	}

	@Override
	public Set<String> getConsumedEvents() {
		return wrapped.getConsumedEvents();
	}

	@Override
	public boolean handlesSelection() {
		return wrapped.handlesSelection();
	}

	@Override
	public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, JavaScriptObject value) {
		return wrapped.isEditing(context, parent, fromJSO(value));
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, JavaScriptObject value,
			NativeEvent event, ValueUpdater<JavaScriptObject> valueUpdater) {
		wrapped.onBrowserEvent(context, parent, fromJSO(value), event, wrappUpdater(valueUpdater));
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, JavaScriptObject value, SafeHtmlBuilder sb) {
		wrapped.render(context, fromJSO(value), sb);
	}

	@Override
	public boolean resetFocus(com.google.gwt.cell.client.Cell.Context context, Element parent, JavaScriptObject value) {
		return wrapped.resetFocus(context, parent, fromJSO(value));
	}

	@Override
	public void setValue(com.google.gwt.cell.client.Cell.Context context, Element parent, JavaScriptObject value) {
		wrapped.setValue(context, parent, fromJSO(value));
	}

	private C fromJSO(JavaScriptObject value) {
		return adapter.fromJSO(value);
	}

	private JavaScriptObject toJSO(C value) {
		return adapter.toJSO(value);
	}

	private ValueUpdater<C> wrappUpdater(ValueUpdater<JavaScriptObject> valueUpdater) {
		return new ValueUpdateAdapter<C>(valueUpdater, this);
	}


}
