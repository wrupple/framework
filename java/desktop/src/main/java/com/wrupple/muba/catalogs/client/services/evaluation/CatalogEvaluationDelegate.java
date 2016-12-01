package com.wrupple.muba.catalogs.client.services.evaluation;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public interface CatalogEvaluationDelegate {
	
	public JsArray<JsCatalogEntry> getCachedFilteredEntries(CatalogDescriptor descriptor, CatalogCache cache, JsFilterData filter, EventBus bus, ClientCatalogCacheManager ccm);
	
	public JsArray<JsCatalogEntry> processJoinData(JsArray<JsCatalogEntry> result, JsFilterData filter, JsArray<JsArrayString> joins,CatalogDescriptor descriptor, ClientCatalogCacheManager ccm) ;

	public boolean matchAgainstFilters(JsCatalogKey entry, JsArray<JsFilterCriteria> filters, CatalogDescriptor descriptor);

	public JsArrayMixed getTranslatedFilterValues(JsArrayMixed rawValues, int dataType, String operator);

	public JsArray<JsCatalogEntry> getCachedEntriesByKeyCriteria(CatalogCache cache, JsFilterCriteria keyCriteria, EventBus bus);

	public void eval(CatalogDescriptor catalog, FieldDescriptor field, JsCatalogKey processedResult,String formula);

	public void eval(CatalogDescriptor catalog, FieldDescriptor field, List<JsCatalogEntry> result, String formula);

	public void eval(CatalogDescriptor descriptor, FieldDescriptor field, JsArray<JsCatalogEntry> result, String formula);

	public void validate(CatalogDescriptor catalog, String fieldId, JsCatalogEntry value, JsArrayString violations);

}
