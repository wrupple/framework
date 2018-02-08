package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.vegetate.client.services.RemoteStorageUnit;
import com.wrupple.vegetate.domain.CatalogEntry;

public class ImageKeyThumbnail extends Image implements HasValue<String> {
	public static final String CUSTOM_IMAGE_SIZE_PROPERTY = "customImageSize";
	private String value;
	private int customSize;
	private DesktopManager dm;
	private StorageManager sm;

	@Inject
	public ImageKeyThumbnail( DesktopManager dm ,StorageManager sm) {
		super();
		this.sm=sm;
		this.dm=dm;
		this.customSize=-1;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value=value;
		if(value!=null){
			RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry> constants = sm.getRemoteStorageUnit(dm.getCurrentActivityHost());
			SafeUri url = constants.getImageUri(dm.getCurrentActivityDomain(),value, customSize);
			super.setUrl(url);
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		setValue(value);
		if(fireEvents){
			ValueChangeEvent.fire(this, getValue());
		}
	}

	public void setCustomSize(int customSize) {
		this.customSize = customSize;
	}

}
