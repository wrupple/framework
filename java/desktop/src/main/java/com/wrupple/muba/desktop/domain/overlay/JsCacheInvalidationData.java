package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.bpm.domain.BusinessEventDTO;

public final class JsCacheInvalidationData extends JavaScriptObject implements
		BusinessEventDTO {


	protected JsCacheInvalidationData() {
		super();
	}

	@Override
	public native String getTimestamp() /*-{
		return this.timestamp;
	}-*/;

	@Override
	public native String getCatalog() /*-{
		return this.catalog;
	}-*/;

	@Override
	public native String getEntryId() /*-{
		return this.entryId;
	}-*/;

	

	@Override
	public native String getName() /*-{
		return this.name;
	}-*/;

	@Override
	public native JsCatalogEntry getEntry() /*-{
		return this.entry;
	}-*/;

	@Override
	public native String getDomain() /*-{
		return this.domain;
	}-*/;

}
