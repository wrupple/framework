package com.wrupple.muba.bpm.shared.services.impl;

import com.wrupple.muba.event.domain.Instrospector;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterCriteria;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


import java.util.Collection;

/**
 * Implemented from GWTFieldConversionStrategyImpl
 */
@Singleton
public class FieldConversionStrategyImpl implements FieldConversionStrategy {

    private final SystemCatalogPlugin access;
    private final ObjectNativeInterface nativeInterface;

    private final FilterNativeInterface filterer;

    @Inject
    public FieldConversionStrategyImpl(SystemCatalogPlugin access, ObjectNativeInterface nativeInterface, FilterNativeInterface filterer) {
        this.access = access;
        this.nativeInterface = nativeInterface;
        this.filterer = filterer;
    }

    public Object getUserReadableCollection(Object arro, List<FilterCriteria> includeCriteria, Instrospector instrospector) {
        if (includeCriteria == null) {
            return arro;
        } else {
            if (arro == null) {
                return null;
            } else {
                List<CatalogEntry> arr = (List<CatalogEntry>) arro;
                //JavaScriptObject o;
                CatalogEntry o;
                boolean match;
                //JsArray<JavaScriptObject> regreso = JavaScriptObject.createArray().cast();
                List<CatalogEntry> regreso = new ArrayList<>(arr.size());
                for (int i = 0; i < arr.size(); i++) {
                    o = arr.get(i);
                    match = matchesCriteria(o, includeCriteria, instrospector);
                    if (match) {
                        // include
                        regreso.add(o);
                    }
                }
                return regreso;
            }
        }
    }





    private boolean matchesCriteria(CatalogEntry o, List<FilterCriteria> includeCriteria, Instrospector instrospector) {
        for (FilterCriteria criteria : includeCriteria) {
            if (matches( criteria, o, instrospector)) {
                return true;
            }
        }
        return false;
    }

    // TRUE IF MATCH AGINST AT LEAST ONE CRITERIA
    private boolean matches(FilterCriteria criteria, CatalogEntry o, Instrospector instrospector) {
        //JsArrayMixed values = criteria.getValuesArray();
        List<Object> values = criteria.getValues();
        //JsArrayString path = criteria.getPathArray();;
        List<String> path = criteria.getPath();
        if (path != null && values != null) {
            if (values.size() > 0 && path.size() > 0) {
                String pathing = path.get(0);
                for (int i = 0; i < values.size(); i++) {
                    if (filterer.jsMatch(pathing, o, values, i, instrospector)) {
                        return true;
                    }
                }
            }
        }
        return false;
    };


    @Override
    public Object convertToPresentableValue(String attr, CatalogEntry elem, List<FilterCriteria> includeCriteria, Instrospector instrospector) {
        Object value =  nativeInterface.getWrappedValue(attr, instrospector,elem,false);
        if (value == null) {
            return null;
        } else if (nativeInterface.isCollection(value)) {
            return getUserReadableCollection(value, includeCriteria, instrospector);
        } else if (nativeInterface.isBoolean(value)) {
            return value;
        } else if (nativeInterface.isNumber(value)) {
            //JSONNumber jsonNumber = value.isNumber();
            //return jsonNumber.toString();
            return nativeInterface.formatNumberValue(value);
        } else if (nativeInterface.isWrappedObject(value)) {
            return value;
        } else {
            try {
                //String string = value.isString().stringValue();
                return nativeInterface.getStringValue(value);
            } catch (Exception e) {
                return nativeInterface.getDefaultValue(value);
            }
        }
    }

    @Override
    public void setAsPersistentValue(Object value, FieldDescriptor field, CatalogEntry jso, Instrospector instrospector) throws ReflectiveOperationException {

        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            access.access().deleteAttribute(jso, field.getFieldId(), instrospector);
        } else if (nativeInterface.isCollection(value)) {
            access.access().setPropertyValue(field,jso,nativeInterface.unwrapAsNativeCollection((Collection) value), instrospector);
        } else if (value instanceof Number) {

            // FIXME some runtimes (browser) may require to unwrap the java object
            /*Number v = (Number) value;
            if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {

                 access.setAttribute(jso, field, v.intValue(),instrospector);
            } else {
                access.setAttribute(jso, field, v.doubleValue(),instrospector);
            }
            */
            access.access().setPropertyValue(field,jso,value, instrospector);
        } else if (value instanceof String) {
            String v = (String) value;
            v = getSendableString(v);
            if (v == null) {
                access.access().deleteAttribute(jso, field.getFieldId(), instrospector);
            }else{
                if(field.isMultiple()){
                    access.access().setPropertyValue(field,jso,nativeInterface.eval(v), instrospector);
                }else{
                    try {
                        switch (dataType) {
                            case CatalogEntry.BOOLEAN_DATA_TYPE:
                                access.access().parseSetBoolean(jso, field, v, instrospector);
                                break;
                            case CatalogEntry.INTEGER_DATA_TYPE:
                                if(field.isKey()){
                                    //TODO distinguish client and server runtime cus KEYS ARE ALWAYS SUPPOSED TO BE STRINGS CLIENT-SIDE,
                                    access.access().setPropertyValue(field,jso,v, instrospector);
                                    //access.setAttribute(jso, fieldId, v);
                                }else{
                                    access.access().parseSetInteger(v,jso,field, instrospector);
                                }
                                break;
                            case CatalogEntry.NUMERIC_DATA_TYPE:
                                access.access().parseSetDouble(v,jso,field, instrospector);
                                break;
                            default:
                                access.access().setPropertyValue(field,jso,v, instrospector);
                        }
                    } catch (Exception e) {
                        access.access().setPropertyValue(field,jso,v, instrospector);
                        //access.setAttribute(jso, fieldId, v);
                    }
                }
            }

        } else {
            access.access().setPropertyValue(field,jso,value, instrospector);
            //access.setAttribute(jso, fieldId, value);
        }
    }

    @Override
    public Object convertToPersistentValue(Object value, FieldDescriptor field) {
        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            return null;
        } else if (nativeInterface.isCollection(value)) {
            return nativeInterface.unwrapAsNativeCollection((Collection)value);
        } else if (value instanceof Number) {
            Number v = (Number) value;
            if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {
                return v.intValue();
            } else {
                return v.doubleValue();
            }
        } else if (value instanceof String) {
            String v = (String) value;
            v = getSendableString(v);
            if (v == null&&CatalogEntry.BOOLEAN_DATA_TYPE!=dataType) {
                return null;
            }

            try {
                switch (dataType) {
                    case CatalogEntry.BOOLEAN_DATA_TYPE:
                        return  "1".equals(v) || Boolean.parseBoolean(v);
                    case CatalogEntry.INTEGER_DATA_TYPE:
                        if(field.isKey()){
                            return v;
                        }
                        return Integer.parseInt(v);
                    case CatalogEntry.NUMERIC_DATA_TYPE:
                        return Double.parseDouble(v);
                        //FIXME attempt to parse a date with system date parser
                    default:
                        return v;
                }
            } catch (Exception e) {
                return v;
            }

        } else {
            return value;
        }
    }

    private String getSendableString(String v) {
        if (v == null || (v.trim().isEmpty())) {
            return null;
        }
        return v.trim();
    }


}

