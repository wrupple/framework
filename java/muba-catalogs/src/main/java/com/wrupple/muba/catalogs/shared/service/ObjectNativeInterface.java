package com.wrupple.muba.catalogs.shared.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.server.service.impl.CatalogManagerImpl;

import java.util.Collection;
import java.util.List;

/**
 * Created by rarl on 1/06/17.
 */
public interface ObjectNativeInterface {
    public FieldAccessStrategy.Session newSession(CatalogEntry sample);
    //previously convertToJavascriptArray
    Object unwrapAsNativeCollection(Collection objects);

    Object eval(String v);

    boolean isSystemObject(Object value);

    boolean isCollection(Object value);

    boolean isBoolean(Object value);

    boolean isNumber(Object value);

    String formatNumberValue(Object value);

    boolean isWrappedObject(Object value);//always false in most runtimes, not in the browser though

    Object getStringValue(Object value);//trim

    Object getDefaultValue(Object value);

    Object getUserReadableCollection(Object value, List<FilterCriteria> includeCriteria);

    Object getWrappedValue(String attr, FieldAccessStrategy.Session session, CatalogEntry elem, boolean b);
}
