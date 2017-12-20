package com.wrupple.muba.catalogs.client.services.evaluation;

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

import java.util.List;

public interface CatalogEvaluationDelegate {

    JsArray<JsCatalogEntry> getCachedFilteredEntries(CatalogDescriptor descriptor, CatalogCache cache, JsFilterData filter, EventBus bus, ClientCatalogCacheManager ccm);

    JsArray<JsCatalogEntry> processJoinData(JsArray<JsCatalogEntry> result, JsFilterData filter, JsArray<JsArrayString> joins, CatalogDescriptor descriptor, ClientCatalogCacheManager ccm);

    boolean matchAgainstFilters(JsCatalogKey entry, JsArray<JsFilterCriteria> filters, CatalogDescriptor descriptor);

    JsArrayMixed getTranslatedFilterValues(JsArrayMixed rawValues, int dataType, String operator);

    JsArray<JsCatalogEntry> getCachedEntriesByKeyCriteria(CatalogCache cache, JsFilterCriteria keyCriteria, EventBus bus);

    void eval(CatalogDescriptor catalog, FieldDescriptor field, JsCatalogKey processedResult, String formula);

    void eval(CatalogDescriptor catalog, FieldDescriptor field, List<JsCatalogEntry> result, String formula);

    void eval(CatalogDescriptor descriptor, FieldDescriptor field, JsArray<JsCatalogEntry> result, String formula);

    void validate(CatalogDescriptor catalog, String fieldId, JsCatalogEntry value, JsArrayString violations);

}
