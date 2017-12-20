package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JavaScriptObject;

public final class PanelTransformationConfig extends JavaScriptObject {
	protected PanelTransformationConfig() {
	}

	public native String getToolbarId()/*-{
										return this.panelAlterationToolbarId;
										}-*/;

	public native String getTarget()/*-{
									return this.panelAlterationTarget;
									}-*/;

	public native String getAlterationCommand() /*-{
												return this.panelAlterationCommand;
												}-*/;

	public native boolean getFireReset() /*-{
											return this.fireReset!=null;
											}-*/;

	public native void setType(String type) /*-{
											this.type=type;
											}-*/;

	public native void setWidget(String widget) /*-{
												this.widget=widget;
												}-*/;
}