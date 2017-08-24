package com.wrupple.muba.catalogs.shared.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 *
 * implemented from a mix of GWTUtils and VegetateStorageUnit and WruppleCatalogEvaluationDelegate
 * Created by rarl on 29/05/17.
 */
public interface FieldAccessStrategy {


    public interface Session {
        void resample(CatalogEntry sample);

        boolean isAccesible();

        void setAccesible(boolean b);
    }

    Session newSession(CatalogEntry sample);

    Object getPropertyValue(FieldDescriptor field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException;

    Object getPropertyValue(String field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException;

    void setPropertyValue(FieldDescriptor field, CatalogEntry entry, Object value,
                          Session session) throws ReflectiveOperationException;

    public CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException;

    CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException;


    boolean isReadableProperty(String foreignKeyValuePropertyName, CatalogEntry e, Session session);

    boolean isWriteableProperty(String string, CatalogEntry sample, Session session);

    void setPropertyValue(String reservedField, CatalogEntry e, Object value,
                          Session session) throws Exception;
    ///TODO implement with GWTUtils

    void deleteAttribute(CatalogEntry jso, String fieldId, FieldAccessStrategy.Session session) throws ReflectiveOperationException;

    void parseSetDouble(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, FieldAccessStrategy.Session session) throws ReflectiveOperationException;

    void parseSetInteger(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, FieldAccessStrategy.Session session) throws ReflectiveOperationException;

    void parseSetBoolean(CatalogEntry jso, FieldDescriptor field, String v, FieldAccessStrategy.Session session) throws ReflectiveOperationException;
}
