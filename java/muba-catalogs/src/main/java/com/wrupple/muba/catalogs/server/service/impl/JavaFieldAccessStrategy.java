package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * evaluation
 * Created by japi on 11/07/17.
 */
@Singleton
public class JavaFieldAccessStrategy implements FieldAccessStrategy {

    protected static final Logger log = LoggerFactory.getLogger(JavaFieldAccessStrategy.class);

    private final Provider<PersistentCatalogEntity> factory;


    private final ObjectNativeInterface nativeInterface;


    @Inject
    public JavaFieldAccessStrategy(Provider<PersistentCatalogEntity> factory, ObjectNativeInterface nativeInterface) {
        this.factory = factory;
        this.nativeInterface = nativeInterface;
    }


    @Override
    public CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException {
        CatalogEntry copy = synthesize(catalog);

        Collection<FieldDescriptor> fields = catalog.getFieldsValues();

        Session session = newSession(copy);

        Object value;
        for (FieldDescriptor field : fields) {
            if (field.isKey() && field.getFieldId().equals(catalog.getKeyField())) {

            } else {
                value = getPropertyValue(field, entry, null, session);
                setPropertyValue(field, copy, value, session);
            }
        }

        return copy;
    }

    @Override
    public boolean isReadableProperty(String foreignKeyValuePropertyName, CatalogEntry e, Session session) {
        if(session.isAccesible()){
            return true;
        }else{
            return this.nativeInterface.isReadable(foreignKeyValuePropertyName,e);
        }

    }

    @Override
    public CatalogEntry synthesize(CatalogDescriptor catalog) throws IllegalAccessException, InstantiationException {
        if (catalog.getClazz() == null || PersistentCatalogEntity.class.equals(catalog.getClazz())) {

            PersistentCatalogEntity target = factory.get();
            target.initCatalog(catalog);
            return target;
        } else {
            Class<?> clazz = catalog.getClazz();
            CatalogEntry target = (CatalogEntry) clazz.newInstance();
            return target;
        }
    }


    @Override
    public Session newSession(CatalogEntry sample) {
        return this.nativeInterface.newSession(sample);
    }

    @Override
    public Object getPropertyValue(FieldDescriptor field, CatalogEntry object,
                                   DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException {
        // log.trace("[READ PROPERTY] {}.{}", catalog.getDistinguishedName(),
        // field.getFieldId());
        /*
		 * if(s==null){ session = new FieldAccessSession(entry instanceof
		 * HasAccesablePropertyValues); }
		 */
        String fieldId = field.getFieldId();
        Object value = null;

        if (localizedObject != null && field.isLocalized()) {
            // support non-string values
            value = localizedObject.getLocalizedFieldValue(fieldId);
        }

		/*
		 * TODO cache in catalog descriptor
		 */
        if (value == null) {
            value = getPropertyValue(fieldId, object, localizedObject, session);

            if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
                value = nativeInterface.getStringValue(value);
            }
        }

        // log.trace("[READ PROPERTY] value = {}", value);
        return value;
    }

    @Override
    public Object getPropertyValue(String fieldId, CatalogEntry object, DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException {
        Object value = null;
        //value = valuedoReadProperty(fieldId, session, object, false);
        if (session.isAccesible()) {
            try {
                value = doGetAccesibleProperty(object, fieldId);
            } catch (ClassCastException e) {
                log.debug("Catalog Property Session Changed State");
                session.setAccesible(false);
                value = nativeInterface.getWrappedValue(fieldId, session, object, true);
            }

        } else {
            try {
                value = nativeInterface.getWrappedValue(fieldId, session, object, false);

            } catch (Throwable e) {

                log.debug("Catalog Property Session Changed State");
                session.setAccesible(true);
                value = doGetAccesibleProperty(object, fieldId);
            }
        }
        return value;
    }


    private Object doGetAccesibleProperty(CatalogEntry object, String fieldId) {
        HasAccesablePropertyValues entry = (HasAccesablePropertyValues) object;
        return entry.getPropertyValue(fieldId);
    }

    @Override
    public void setPropertyValue(FieldDescriptor field, CatalogEntry object, Object value,
                                 Session session) throws ReflectiveOperationException {
        // log.trace("[WRITE PROPERTY] {}.{}", catalog.getDistinguishedName(),
        // field.getFieldId());
        // log.trace("[WRITE PROPERTY] value = {}", value);
		/*
		 * if(s==null){ session = new FieldAccessSession(entry instanceof
		 * HasAccesablePropertyValues); }
		 */
        String fieldId = field.getFieldId();

        if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
            value = nativeInterface.processRawLongString((String) value);
        }
        setPropertyValue(fieldId, object, value, session);

    }

    private void doSetAccesibleProperty(CatalogEntry object, String fieldId, Object value) {
        HasAccesablePropertyValues entry = (HasAccesablePropertyValues) object;
        entry.setPropertyValue(value, fieldId);
    }


    private void doBeanSet(Session session, CatalogEntry object, String fieldId, Object value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        nativeInterface.setProperty(object, fieldId, value,session);
    }

    @Override
    public boolean isWriteableProperty(String property, CatalogEntry entry, Session session) {
        if (session.isAccesible()) {
            return true;
        }
        return nativeInterface.isWriteable(entry, property);
    }

    @Override
    public void setPropertyValue(String fieldId, CatalogEntry object, Object value,
                                 Session session) throws ReflectiveOperationException {
        if (session.isAccesible()) {
            try {
                doSetAccesibleProperty(object, fieldId, value);
            } catch (ClassCastException e) {
                log.error("reading field",e);
                session.setAccesible(false);
                try {
                    doBeanSet(session, object, fieldId, value);
                } catch (IllegalAccessException ee) {
                    throw new IllegalArgumentException("access field " + fieldId, ee);
                } catch (InvocationTargetException ee) {
                    throw new IllegalArgumentException("access field " + fieldId, ee);
                }

            }
        } else {
            try {
                doBeanSet(session, object, fieldId, value);
            } catch (Exception e) {
                session.setAccesible(true);
                log.error("reading field",e);
                try {
                    doSetAccesibleProperty(object, fieldId, value);
                } catch (ClassCastException ee) {
                    throw new IllegalArgumentException("access field " + fieldId, ee);
                }

            }

        }
    }

    @Override
    public void deleteAttribute(CatalogEntry jso, String fieldId, FieldAccessStrategy.Session session) throws ReflectiveOperationException {
        setPropertyValue(fieldId, jso, null, session);
    }

    @Override
    public void parseSetDouble(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Session session) throws ReflectiveOperationException {
        setPropertyValue(fieldId, jso, Double.parseDouble(rawValue), session);
    }

    @Override
    public void parseSetInteger(String rawValue, CatalogEntry jso, FieldDescriptor fieldId, Session session) throws ReflectiveOperationException {
        setPropertyValue(fieldId, jso, Integer.parseInt(rawValue), session);
    }

    @Override
    public void parseSetBoolean(CatalogEntry jso, FieldDescriptor fieldId, String rawValue, Session session) throws ReflectiveOperationException {
        setPropertyValue(fieldId, jso, null == null ? false : Boolean.parseBoolean(rawValue), session);
    }

}
