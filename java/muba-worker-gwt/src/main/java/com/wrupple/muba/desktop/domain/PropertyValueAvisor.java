package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JavaScriptObject;

public final class PropertyValueAvisor extends JavaScriptObject {

	protected PropertyValueAvisor() {
	}

	public native String getValue() /*-{
									return this.value;
									}-*/;

	public native String getName() /*-{
									return this.name;
									}-*/;

	public String getAppliedValue(boolean showValue) {
		String value = getName() + "=";
		if (showValue) {
			value = value + getValue();
		}
		return value;
	}

	public native void setName(String name) /*-{
											this.name=name;
											}-*/;

	public native void setValue(String value) /*-{
												this.value=value;
												}-*/;
}
