package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JsArrayMixed;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.vegetate.domain.FilterData;


public interface CatalogPlaceInterpret {

    String getPlaceCatalog(DesktopPlace item);

    String getCurrentPlaceEntry(DesktopPlace item);

    FilterData getCurrentPlaceFilterData(DesktopPlace parameter);

    JsArrayMixed getPreselectedKeys(String rawKeysParamValue);

    String getRawPreselectedKeys(JsArrayMixed keys);
}
