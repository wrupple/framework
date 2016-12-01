package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;

@SuppressWarnings("serial")
public final class JsCatalogActionRequest extends JavaScriptObject implements CatalogActionRequest {

	protected JsCatalogActionRequest() {
	}

	public native static JsCatalogActionRequest newRequest(String domain_, String locale_, String catalog_, String action_, String entry_
			, String format_,JsCatalogEntry catalogEntry_, JsFilterData filter_) /*-{
		return {
			domain : domain_,
			locale: locale_,
			catalog : catalog_,
			action : action_,
			entry : entry_,
			format : format_,
			filter : filter_,
			catalogEntry : catalogEntry_
		};
	}-*/;
	
	/*
	 {"domain" : "user","catalog" : "Playera","action" : "read","entry" : "1"}
	 */

	@Override
	public native String getAction() /*-{
		return this.action;
	}-*/;

	@Override
	public native String getFormat() /*-{
		return this.format;
	}-*/;

	@Override
	public native JsFilterData getFilter() /*-{
		return this.filter;
	}-*/;

	@Override
	public native JsCatalogEntry getCatalogEntry() /*-{
		return this.catalogEntry;
	}-*/;

	@Override
	public native String getEntry() /*-{
		return this.entry;
	}-*/;

	@Override
	public native String getCatalog() /*-{
		return this.catalog;
	}-*/;

	@Override
	public native String getDomain() /*-{
		return this.domain;
	}-*/;

	public native void setAction(String action) /*-{
		return this.action = action;
	}-*/;

	public native void setFormat(String format) /*-{
		return this.format = format;
	}-*/;

	public native void setFilter(JsFilterData filter) /*-{
		return this.filter = filter;
	}-*/;

	public native void setCatalogEntry(JsCatalogEntry catalogEntry) /*-{
		return this.catalogEntry = catalogEntry;
	}-*/;

	public native void setEntry(String entry) /*-{
		return this.entry = entry;
	}-*/;

	public native void setCatalog(String catalog) /*-{
		return this.catalog = catalog;
	}-*/;

	public native void setDomainFromString(String domain) /*-{
		return this.domain = domain;
	}-*/;

	@Override
	public native String getLocale() /*-{
		return this.locale;
	}-*/;

}
