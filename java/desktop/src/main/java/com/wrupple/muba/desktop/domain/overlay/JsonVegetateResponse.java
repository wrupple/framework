package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsonVegetateResponse extends JavaScriptObject {

	protected JsonVegetateResponse() {
	}

	
	//JsCatalogActionResult
	public native JavaScriptObject getNamedResult(String actionName)/*-{
		return this[actionName];
	}-*/;
}
