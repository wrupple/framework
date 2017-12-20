package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.vegetate.domain.Entity;


@SuppressWarnings("serial")
public class JsEntity extends JsCatalogKey implements Entity {

	protected JsEntity() {
	}

	public final native String getStringValue() /*-{
		if (this.value == undefined || this.value == null) {
			return null;
		} else {
			return String(this.value);
		}
	}-*/;


}
