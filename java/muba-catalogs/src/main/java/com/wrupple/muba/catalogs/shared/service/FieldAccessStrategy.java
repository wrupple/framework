package com.wrupple.muba.catalogs.shared.service;


import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospector;

/**
 *
 * implemented from a mix of GWTUtils and VegetateStorageUnit and WruppleCatalogEvaluationDelegate
 * Created by rarl on 29/05/17.
 */
public interface FieldAccessStrategy {


    Instrospector newSession(CatalogEntry sample);

    Object getPropertyValue(FieldDescriptor field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Instrospector instrospector) throws ReflectiveOperationException;

    Object getPropertyValue(String field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Instrospector instrospector) throws ReflectiveOperationException;

    void setPropertyValue(FieldDescriptor field, CatalogEntry entry, Object value,
                          Instrospector instrospector) throws ReflectiveOperationException;

    public CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException;

    CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException;


    boolean isReadableProperty(String foreignKeyValuePropertyName, CatalogEntry e, Instrospector instrospector);

    boolean isWriteableProperty(String string, CatalogEntry sample, Instrospector instrospector);

    void setPropertyValue(String reservedField, CatalogEntry e, Object value,
                          Instrospector instrospector) throws Exception;
    ///TODO implement with GWTUtils

    void deleteAttribute(CatalogEntry jso, String fieldId, Instrospector instrospector) throws ReflectiveOperationException;

    void parseSetDouble(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Instrospector instrospector) throws ReflectiveOperationException;

    void parseSetInteger(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Instrospector instrospector) throws ReflectiveOperationException;

    void parseSetBoolean(CatalogEntry jso, FieldDescriptor field, String v, Instrospector instrospector) throws ReflectiveOperationException;
}
