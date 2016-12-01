package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JsArrayMixed;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.vegetate.domain.FilterData;


public interface CatalogPlaceInterpret {

	public String getPlaceCatalog(DesktopPlace item);
	
	public String getCurrentPlaceEntry(DesktopPlace item);
	
	public FilterData getCurrentPlaceFilterData(DesktopPlace parameter);

	public JsArrayMixed getPreselectedKeys(String rawKeysParamValue);

	public String getRawPreselectedKeys(JsArrayMixed keys);
}
