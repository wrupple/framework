package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.event.server.service.ObjectNativeInterface;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by rarl on 5/06/17.
 */
@Singleton
public class JavaObjectNativeInterface implements ObjectNativeInterface {


    protected static final Logger log = LogManager.getLogger(JavaObjectNativeInterface.class);
    private final LargeStringFieldDataAccessObject delegate;

        //TODO use common instance (dfined in application module)
    private final PropertyUtilsBean bean;

    @Inject
    public JavaObjectNativeInterface(LargeStringFieldDataAccessObject delegate) {
        this.delegate = delegate;
        this.bean  = new PropertyUtilsBean();
    }

    @Override
    public Instrospection newSession(CatalogEntry sample) {
        FieldAccessInstrospection session = new FieldAccessInstrospection();
        session.resample(sample);
        return session;
    }

    private class FieldAccessInstrospection implements Instrospection {
        boolean accesible;

        @Override
        public void resample(CatalogEntry sample) {
            isSystemObject(sample);
        }

        @Override
        public boolean isAccesible() {
            return accesible;
        }

        @Override
        public void setAccesible(boolean b) {
            this.accesible=b;
        }

        // use PropertyUtilsBean (bean utils) and dump srping
        private Object getPropertyValue2(Object bean, String property) throws IntrospectionException,
                IllegalArgumentException,IllegalAccessException, InvocationTargetException   {
            Class<?> beanClass = bean.getClass();
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(beanClass, property);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("No such property " + property + " for " + beanClass + " exists");
            }

            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                throw new IllegalStateException("No getter available for property " + property + " on " + beanClass);
            }
            return readMethod.invoke(bean);
        }

        private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyname)
                throws IntrospectionException {
            PropertyDescriptor propertyDescriptor = getDescriptorFromCache(beanClass, propertyname);

            if (propertyDescriptor == null) {
                BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
                    if (currentPropertyDescriptor.getName().equals(propertyname)) {
                        propertyDescriptor = currentPropertyDescriptor;
                    }

                }
            }

            return propertyDescriptor;
        }

        private PropertyDescriptor getDescriptorFromCache(Class<?> beanClass, String propertyname) {
            // FIXME cache (in outer class)
            return null;
        }

        private Object getPropertyValue(CatalogEntry object, String fieldId)
                throws  IllegalAccessException, InvocationTargetException, NoSuchMethodException {

            return bean.getProperty(object, fieldId);
        }

    }




    @Override
    public Object unwrapAsNativeCollection(Collection objects) {
        return objects;
    }

    @Override
    public boolean isCollection(Object value) {
        return value instanceof Collection;
    }

    @Override
    public boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public boolean isNumber(Object value) {
        return value instanceof Number;
    }

    @Override
    public String formatNumberValue(Object value) {
        //JSONNumber jsonNumber = value.isNumber();
        //return jsonNumber.toString();
        return value.toString();
    }

    @Override
    public boolean isWrappedObject(Object value) {
        return !(isBoolean(value) || isNumber(value) || isCollection(value) || value instanceof String);
    }

    @Override
    public Type getLargeStringClass() {
        //unless otherwise
        return String.class;
    }

    @Override
    public String getStringValue(Object value) {
        return /*ina java jre we expect Strings*/(String) value;
    }

    @Override
    public Object processRawLongString(String s) {
        return s;
    }

    @Override
    public Object getDefaultValue(Object value) {
        return value;
    }

    @Override
    public Object getWrappedValue(String fieldId, Instrospection instrospection, CatalogEntry object, boolean silentFail) {

            try {
                return ((FieldAccessInstrospection) instrospection).getPropertyValue(object,fieldId);
            } catch (Exception e) {
                if(silentFail){
                    return null;
                }else{
                    log.error("failed reading field {}",fieldId);
                    throw new IllegalArgumentException(e);
                }

            }


    }

    @Override
    public void setProperty(CatalogEntry object, String fieldId, Object value, Instrospection instrospection) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        bean.setProperty(object,fieldId,value);
    }

    @Override
    public boolean isWriteable(CatalogEntry entry, String property) {
        return bean.isWriteable(entry,property);
    }

    @Override
    public Object getPropertyValue(CatalogEntry o, String pathing, Instrospection instrospection) {
        if(instrospection.isAccesible()){
            return ((HasAccesablePropertyValues)o).getPropertyValue(pathing);
        }else{
            return getWrappedValue(pathing, instrospection,o,false);
        }
    }

    @Override
    public boolean isReadable(String foreignKeyValuePropertyName, CatalogEntry e) {
        return bean.isReadable(e,foreignKeyValuePropertyName);
    }


    private Object goBeanGet(FieldAccessInstrospection session, CatalogEntry object, String fieldId)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException , IntrospectionException,
            NoSuchMethodException {
        return session.getPropertyValue(object, fieldId);
    }

    private Object doGetAccesibleProperty(CatalogEntry object, String fieldId) {
        HasAccesablePropertyValues entry = (HasAccesablePropertyValues) object;
        return entry.getPropertyValue(fieldId);
    }

    @Override
    public Object eval(String v) {
        throw new RuntimeException("safe string evaluation not supported un Java");
    }

    @Override
    public boolean isSystemObject(Object sample) {
        if (sample == null) {
            return true;
        } else {
            return sample instanceof HasAccesablePropertyValues;
        }
    }






}
