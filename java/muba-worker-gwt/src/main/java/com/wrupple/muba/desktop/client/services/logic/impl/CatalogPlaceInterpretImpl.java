package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.logic.URLFilterDataSerializationService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.FilterData;

@Singleton
public class CatalogPlaceInterpretImpl implements CatalogPlaceInterpret {

	URLFilterDataSerializationService serialization;
	@Inject
	public CatalogPlaceInterpretImpl(
			URLFilterDataSerializationService serialization) {
		super();
		
		this.serialization = serialization;
	}

	@Override
	public String getPlaceCatalog(DesktopPlace item) {
		//transactional activities properly set url found properties into the parameter Map
		//if (taskTokens == null) {
		//	taskTokens = UrlParser.DEFAULT_FORM_TASK_TOKENS;
		//}
		
		//if return value is still null try to find the value in query paramenters
		return item.getProperty(CatalogActionRequest.CATALOG_ID_PARAMETER);
	}

	@Override
	public String getCurrentPlaceEntry(DesktopPlace item) {
		return item.getProperty(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
	}

	@Override
	public FilterData getCurrentPlaceFilterData(DesktopPlace parameter) {
		DesktopPlace place = new DesktopPlace(parameter);
		FilterData filter;
		try {
			filter = serialization.deserialize(place.getProperty(CatalogActionRequest.FILTER_DATA_PARAMETER));
		} catch (Exception e) {
			filter=null;
		}
		if(filter==null){
			filter = JsFilterData.newFilterData();
		}
		return filter;
	}

	@Override
	public JsArrayMixed getPreselectedKeys(String rawKeysParamValue) {
		rawKeysParamValue = rawKeysParamValue.trim();
		if(!rawKeysParamValue.contains(" ")){
			String[] tokens =  rawKeysParamValue.split(",");
			//TODO potentially dangerous, since url's can be dirty
			JsArrayMixed regreso = JavaScriptObject.createArray().cast();
			for(String t: tokens){
				regreso.push(t);
			}
			return regreso;
		}
		return null;
	}

	@Override
	public String getRawPreselectedKeys(JsArrayMixed keys) {
		StringBuilder builder = new StringBuilder(keys.length()*11);
		for(int i = 0 ; i< keys.length(); i++){
			builder.append(keys.getString(i));
			if(i<keys.length()-1){
				builder.append(',');
			}
		}
		
		return builder.toString();
	}

}
