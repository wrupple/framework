package com.wrupple.muba.catalogs.shared.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import java.util.Collection;
import java.util.List;

/**
 *
 * implemented from a mix of GWTUtils and VegetateStorageUnit and WruppleCatalogEvaluationDelegate
 * Created by rarl on 29/05/17.
 */
public interface FieldAccessStrategy {

    void setAttribute(CatalogEntry jso, FieldDescriptor fieldId, int v, Session session);

    void setAttribute(CatalogEntry jso, FieldDescriptor fieldId, double v, Session session);

    Object getPropertyValue(Object o, String pathing, Session session);

    public interface Session {
        void resample(CatalogEntry sample);
    }

    Session newSession(CatalogEntry sample);

    Object getPropertyValue(FieldDescriptor field, CatalogEntry entry,
                            DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException;

    void setPropertyValue(FieldDescriptor field, CatalogEntry entry, Object value,
                          Session session) throws ReflectiveOperationException;

    public CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException;

    CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException;

    boolean isWriteableProperty(String string, CatalogEntry sample, Session session);

    void setPropertyValue(String reservedField, CatalogEntry e, Object value,
                          Session session) throws Exception;

    ///TODO implement with GWTUtils

    void deleteAttribute(CatalogEntry jso, String fieldId, FieldAccessStrategy.Session session);

    void parseSetDouble(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Session session);

    void parseSetInteger(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Session session);

    void parseSetBoolean(CatalogEntry jso, FieldDescriptor field, String v, Session session);


    //FIXME this function is part of a distinct superset
    Object userReadableValue(CatalogEntry elem, String attr, List<FilterCriteria> includeCriteria, FieldAccessStrategy.Session session);
}
