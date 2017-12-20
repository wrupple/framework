package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.catalogs.domain.CatalogActionResult;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

import java.util.List;

public final class JsCatalogActionResult extends JavaScriptObject implements CatalogActionResult {

	protected JsCatalogActionResult() {
	}

	public native JsJavaExceptionOverlay asException()/*-{
		return this;
	}-*/;

	public native JsArrayString asList()/*-{
		return this;
	}-*/;

	@Override
	public List<JsVegetateColumnResultSet> getResponse() {
		JsArray<JavaScriptObject> arr = getResponseAsJSOList();
		if (arr == null) {
			return null;
		} else {
			JsArray<JsVegetateColumnResultSet> cast = arr.cast();
			return JsArrayList.arrayAsList(cast);
		}
    }

    public native JsArray<JavaScriptObject> getResponseAsJSOList() /*-{
        return this.response;
	}-*/;

	public native JsArrayBoolean getResponseAsBooleanList() /*-{
		return this.response;
	}-*/;

	@Override
	public List<String> getWarnings() {
		return JsArrayList.arrayAsListOfString(getWarningsArray());
	}

	public native JsArrayString getWarningsArray() /*-{
		return this.warnings;
	}-*/;

	@Override
	public Long getResponseTimestamp() {
		String raw = GWTUtils.getAttribute(this, "responseTimestamp");
		if (raw == null) {
			return null;
		} else {
			return Long.parseLong(raw);
		}
	}

	public native void setCatalogEntries(JsArray<JavaScriptObject> catalogEntries) /*-{
		this.catalogEntries = catalogEntries;
	}-*/;

	public native JsArray<JavaScriptObject> getCatalogEntries()/*-{
		return this.catalogEntries;
	}-*/;

}