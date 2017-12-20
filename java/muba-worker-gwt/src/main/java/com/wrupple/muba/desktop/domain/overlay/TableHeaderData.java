package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public final class TableHeaderData extends JavaScriptObject {
	protected TableHeaderData() {

	}

	public native JsFilterCriteria getCriteria()/*-{
		return this.criteria;
	}-*/;

	public native JsFieldDescriptor getDescriptor()/*-{
		return this.descriptor;
	}-*/;

	public native void setDescriptor(JsFieldDescriptor field) /*-{
		this.descriptor = field;
	}-*/;

	public native void setCriteria(JsFilterCriteria criteria) /*-{
		this.criteria = criteria;
	}-*/;
}
