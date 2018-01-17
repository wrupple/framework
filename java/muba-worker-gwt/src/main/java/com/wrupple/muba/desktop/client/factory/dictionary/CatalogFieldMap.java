package com.wrupple.muba.desktop.client.factory.dictionary;

import com.wrupple.muba.desktop.client.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;

/**
 * Vie rebinding, this becomes the default widgetter for Catalog Forms to create
 * widgets to represent the data contained in catalog entries.
 * 
 * an instance of a WidgetCreator/Field/ is required for each field type
 * Creating a widget setRuntimeContext no Field parameters results in a writeable widget
 * useful for update and create forms. Creating a widget setRuntimeContext a Field parameter
 * results in a read-only widget useful for summary and detail views.
 * 
 * 
 * @author japi
 * 
 */
public interface CatalogFieldMap extends ServiceDictionary<CatalogFormFieldProvider> {
	
	String BASIC_CELL = "genericValue";

}
