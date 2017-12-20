package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.services.logic.ConfigurationConstants;

public class ConfigurationConstantsImpl implements ConfigurationConstants {

	public ConfigurationConstantsImpl(){
	}
	
	@Override
	public native JavaScriptObject getIconBrowser(String cellSize,String marginCentered,String catalog,String celValue) /*-{
		if(celValue==null){
			celValue = "icon";
		}
		return {widget:"layout",layout:"grid",cellWrapperClass:"icon-browser-cell",autoMargin: marginCentered ,transition:"magnetic",cell:celValue,cellSize:cellSize,"catalog":catalog};
	}-*/;

}
