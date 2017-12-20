package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.vegetate.domain.FilterDataOrdering;

public final class JsFilterDataOrdering extends JavaScriptObject implements FilterDataOrdering {

	protected JsFilterDataOrdering() {
		super();
	}

	@Override
	public native void setAscending(boolean ascending) /*-{
		this.ascending = ascending;
	}-*/;

	@Override
	public native void setField(String field) /*-{
		this.field = field;
	}-*/;

	@Override
	public native String getField() /*-{
		return this.field;
	}-*/;

	@Override
	public native boolean isAscending() /*-{
		return this.ascending;
	}-*/;

	public static JsFilterDataOrdering newFilterDataOrdering() {
		JsFilterDataOrdering regreso = createObject().cast();
		return regreso;
	}

}
