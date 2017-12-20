package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JsArrayNumber;
import com.wrupple.muba.desktop.domain.WruppleImageMetadata;

import java.util.List;

@SuppressWarnings("serial")
public final class ImageMetadata extends JsCatalogEntry implements
		WruppleImageMetadata {

	protected ImageMetadata() {
		super();
	}


	@Override
	public List<Long> getTags() {
		return JsCatalogEntry.convertLongArray(getTagsArray());
	}

	public native JsArrayNumber getTagsArray() /*-{
		if(this.tags==null){
			this.tags =new Array();
		}
		return this.tags;
	}-*/;

	@Override
	public native String getBlobKey() /*-{
		return this.blobKey;
	}-*/;

	public native void setBlobKey(String id) /*-{
		this.blobKey = id;
	}-*/;


	public native void setTagsArray(JsArrayNumber tags) /*-{
		this.tags = tags;
	}-*/;
}
