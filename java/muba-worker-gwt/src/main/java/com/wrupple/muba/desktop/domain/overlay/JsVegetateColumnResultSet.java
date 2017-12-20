package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.vegetate.domain.VegetateResultSet;

@SuppressWarnings("serial")
public final class JsVegetateColumnResultSet extends JsCatalogKey implements
		VegetateResultSet {


	protected JsVegetateColumnResultSet() {
		super();
	}


	
	public native JsArrayString getColumnNames()/*-{
		if(this.contents==null){
			return null;
		}
		var regreso = new Array();
		for ( var key in this.contents) {
			regreso.push(key);
		}
		return regreso;
	}-*/;

	public native JsArrayMixed getColumn(String fieldId) /*-{
		return this.contents[fieldId];
	}-*/;



	@Override
	public native String getCursor() /*-{
		return this.cursor;
	}-*/;


}
