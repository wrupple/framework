package com.wrupple.muba.catalogs.shared.service.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.FilterNativeInterface;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rarl on 5/06/17.
 */
@Singleton
public class JavaObjectNativeInterface implements ObjectNativeInterface {


    protected static final Logger log = LoggerFactory.getLogger(JavaObjectNativeInterface.class);


    private final Provider<FilterNativeInterface> filterer;
    private final PropertyUtilsBean bean;

    @Inject
    public JavaObjectNativeInterface(Provider<FilterNativeInterface> filterer) {
        this.filterer = filterer;
        this.bean  = new PropertyUtilsBean();
    }

    @Override
    public FieldAccessStrategy.Session newSession(CatalogEntry sample) {
        FieldAccessSession session = new FieldAccessSession();
        session.resample(sample);
        return session;
    }

    private class FieldAccessSession implements FieldAccessStrategy.Session {
        boolean accesible;

        @Override
        public void resample(CatalogEntry sample) {
            isSystemObject(sample);
        }

        // use PropertyUtilsBean (bean utils) and dump srping
        private Object getPropertyValue2(Object bean, String property) throws IntrospectionException,
                IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

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
    public Object getStringValue(Object value) {
        return value.toString();
    }

    @Override
    public Object getDefaultValue(Object value) {
        return value;
    }

    @Override
    public Object getWrappedValue(String fieldId, FieldAccessStrategy.Session session, CatalogEntry object, boolean silentFail) {
        return valuedoReadProperty(fieldId, (FieldAccessSession) session,object, silentFail);
    }



    private Object valuedoReadProperty(String fieldId, FieldAccessSession session, CatalogEntry object,
                                       boolean silentFail) {
        if (session.accesible) {
            try {
                return doGetAccesibleProperty(object, fieldId);
            } catch (ClassCastException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    log.debug("Catalog Property Session Changed State");
                    session.accesible = false;
                    return goBeanGet(session, object, fieldId);
                } catch (IllegalArgumentException ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                } catch (IllegalAccessException ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                } catch (InvocationTargetException ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                } catch (IntrospectionException ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                } catch (NoSuchMethodException ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            }

        } else {
            try {
                return goBeanGet(session, object, fieldId);
            } catch (IllegalArgumentException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    log.debug("Catalog Property Session Changed State");
                    session.accesible = true;
                    return doGetAccesibleProperty(object, fieldId);
                } catch (Exception ee) {

                    log.debug("Access", e);
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            } catch (IllegalAccessException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    session.accesible = true;
                    return doGetAccesibleProperty(object, fieldId);
                } catch (Exception ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            } catch (InvocationTargetException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    session.accesible = true;
                    return doGetAccesibleProperty(object, fieldId);
                } catch (Exception ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            } catch (IntrospectionException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    session.accesible = true;
                    return doGetAccesibleProperty(object, fieldId);
                } catch (Exception ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            } catch (NoSuchMethodException e) {
                if (silentFail) {
                    return null;
                }
                try {
                    session.accesible = true;
                    return doGetAccesibleProperty(object, fieldId);
                } catch (Exception ee) {
                    throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
                }
            }
        }
    }
    private Object goBeanGet(FieldAccessSession session, CatalogEntry object, String fieldId)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException,
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



    @Override
    public Object getUserReadableCollection(Object arro, List<FilterCriteria> includeCriteria) {
        if (includeCriteria == null) {
            return arro;
        } else {
            if (arro == null) {
                return null;
            } else {
                List<Object> arr = (List<Object>) arro;
                //JavaScriptObject o;
                Object o;
                boolean match;
                //JsArray<JavaScriptObject> regreso = JavaScriptObject.createArray().cast();
                List<Object> regreso = new ArrayList<>(arr.size());
                FieldAccessStrategy.Session session = new FieldAccessSession();
                for (int i = 0; i < arr.size(); i++) {
                    o = arr.get(i);
                    match = matchesCriteria(o, includeCriteria, session);
                    if (match) {
                        // include
                        regreso.add(o);
                    }
                }
                return regreso;
            }
        }
    }



    private boolean matchesCriteria(Object o, List<FilterCriteria> includeCriteria, FieldAccessStrategy.Session session ) {
        for (FilterCriteria criteria : includeCriteria) {
            if (matches( criteria, o,session)) {
                return true;
            }
        }
        return false;
    }


    // TRUE IF MATCH AGINST AT LEAST ONE CRITERIA
    private boolean matches(FilterCriteria criteria, Object o, FieldAccessStrategy.Session session) {
        //JsArrayMixed values = criteria.getValuesArray();
        List<Object> values = criteria.getValues();
        //JsArrayString path = criteria.getPathArray();
        FilterNativeInterface delterDelegate = filterer.get();
        List<String> path = criteria.getPath();
        if (path != null && values != null) {
            if (values.size() > 0 && path.size() > 0) {
                String pathing = path.get(0);
                for (int i = 0; i < values.size(); i++) {
                    if (delterDelegate.jsMatch(pathing, o, values, i,session)) {
                        return true;
                    }
                }
            }
        }
        return false;
    };


}
