package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;

import java.util.List;

/**
 * Created by rarl on 29/05/17.
 */
public interface FieldAccessStrategy {
    ///TODO implement with GWTUtils

    void deleteAttribute(CatalogEntry jso, String fieldId);

    Object convertToJavaScriptArray(Object[] objects);

    void setAttribute(CatalogEntry jso, String fieldId, Object aVoid);

    Object eval(String v);

    boolean isSystemObject(Object value);

    Object userReadableValue(CatalogEntry elem, String attr, List<FilterCriteria> includeCriteria);

    void parseSetDouble(String rawValue, CatalogEntry jso, String fieldId);

    void parseSetInteger(String rawValue, CatalogEntry jso, String fieldId);
}
