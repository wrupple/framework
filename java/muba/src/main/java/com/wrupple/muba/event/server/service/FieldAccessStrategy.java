package com.wrupple.muba.event.server.service;


import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.DistributiedLocalizedEntry;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.IntrospectionStrategy;

/**
 *
 * implemented from a mix of GWTUtils and VegetateStorageUnit and WruppleCatalogEvaluationDelegate
 * Created by rarl on 29/05/17.
 */
public interface FieldAccessStrategy extends IntrospectionStrategy {


    Object getPropertyValue(FieldDescriptor field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Instrospection instrospection) throws ReflectiveOperationException;

    Object getPropertyValue(String field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Instrospection instrospection) throws ReflectiveOperationException;

    void setPropertyValue(FieldDescriptor field, CatalogEntry entry, Object value,Instrospection instrospection) throws ReflectiveOperationException;

    CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException;

    CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException;

    boolean catalogEquals(CatalogDescriptor catalog, CatalogEntry one, CatalogEntry other,Instrospection instrospection) throws ReflectiveOperationException ;

    boolean isReadableProperty(String foreignKeyValuePropertyName, CatalogEntry e, Instrospection instrospection);

    boolean isWriteableProperty(String string, CatalogEntry sample, Instrospection instrospection);

    void setPropertyValue(String reservedField, CatalogEntry e, Object value,
                          Instrospection instrospection) throws Exception;
    ///TODO implement with GWTUtils

    void deleteAttribute(CatalogEntry jso, String fieldId, Instrospection instrospection) throws ReflectiveOperationException;

    void parseSetDouble(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Instrospection instrospection) throws ReflectiveOperationException;

    void parseSetInteger(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Instrospection instrospection) throws ReflectiveOperationException;

    void parseSetBoolean(CatalogEntry jso, FieldDescriptor field, String v, Instrospection instrospection) throws ReflectiveOperationException;


    void copy(CatalogEntry origin, CatalogEntry destiny,CatalogDescriptor catalog) throws ReflectiveOperationException;
}
