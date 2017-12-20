package com.wrupple.vegetate.client.services;

import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateColumnResultSet;

public interface CatalogEntryAssembler {

    JsArray<JsCatalogEntry> processResultSet(JsVegetateColumnResultSet resultSet, String catalogid);

}
