package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Instrospection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by rarl on 1/06/17.
 */
public interface ObjectNativeInterface extends LargeStringFieldDataAccessObject{

    public Instrospection newSession(CatalogEntry sample);
    //previously convertToJavascriptArray
    Object unwrapAsNativeCollection(Collection objects);

    Object eval(String v);

    boolean isSystemObject(Object value);

    boolean isCollection(Object value);

    boolean isBoolean(Object value);

    boolean isNumber(Object value);

    String formatNumberValue(Object value);

    boolean isWrappedObject(Object value);//always false in most runtimes, not in the browser though

    Object getDefaultValue(Object value);

    Object getWrappedValue(String fieldId, Instrospection instrospection, CatalogEntry object, boolean silentFail);

    void setProperty(CatalogEntry object, String fieldId, Object value, Instrospection instrospection) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;

    boolean isWriteable(CatalogEntry entry, String property);

    Object getPropertyValue(CatalogEntry o, String pathing, Instrospection instrospection);

    boolean isReadable(String foreignKeyValuePropertyName, CatalogEntry e);
}
