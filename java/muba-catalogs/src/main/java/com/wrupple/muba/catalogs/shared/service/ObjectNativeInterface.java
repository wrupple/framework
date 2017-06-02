package com.wrupple.muba.catalogs.shared.service;

import java.util.Collection;

/**
 * Created by rarl on 1/06/17.
 */
public interface ObjectNativeInterface {
    //previously convertToJavascriptArray
    Object convertToNativeArray(Collection objects);

    Object eval(String v);

    boolean isSystemObject(Object value);

    boolean isCollection(Object value);

    boolean isBoolean(Object value);

    boolean isNumber(Object value);

    String numberStringRepresentation(Object value);

    boolean isWrappedObject();//always false in most runtimes, not in the browser though

    Object getStringValue(Object value);//trim

    Object getDefaultValue(Object value);
}
