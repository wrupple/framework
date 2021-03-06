package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.FilterCriteria;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import com.wrupple.muba.event.server.service.ObjectNativeInterface;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implemented from GWTFieldConversionStrategyImpl
 */
@Singleton
public class FieldConversionStrategyImpl implements FieldConversionStrategy {

    private final FieldAccessStrategy access;
    private final ObjectNativeInterface nativeInterface;

    private final FilterNativeInterface filterer;

    @Inject
    public FieldConversionStrategyImpl(FieldAccessStrategy access, ObjectNativeInterface nativeInterface, FilterNativeInterface filterer) {
        this.access = access;
        this.nativeInterface = nativeInterface;
        this.filterer = filterer;
    }

    public Object getUserReadableCollection(Object arro, List<FilterCriteria> includeCriteria, Instrospection instrospection) {
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
                    match = matchesCriteria(o, includeCriteria, instrospection);
                    if (match) {
                        // include
                        regreso.add(o);
                    }
                }
                return regreso;
            }
        }
    }


    private boolean matchesCriteria(CatalogEntry o, List<FilterCriteria> includeCriteria, Instrospection instrospection) {
        for (FilterCriteria criteria : includeCriteria) {
            if (matches(criteria, o, instrospection)) {
                return true;
            }
        }
        return false;
    }

    // TRUE IF MATCH AGINST AT LEAST ONE CRITERIA
    private boolean matches(FilterCriteria criteria, CatalogEntry o, Instrospection instrospection) {
        //JsArrayMixed values = criteria.getValuesArray();
        List<Object> values = criteria.getValues();
        //JsArrayString path = criteria.getPathArray();;
        List<String> path = criteria.getPath();
        if (path != null && values != null) {
            if (values.size() > 0 && path.size() > 0) {
                String pathing = path.get(0);
                for (int i = 0; i < values.size(); i++) {
                    if (filterer.jsMatch(pathing, o, values, i, instrospection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public Object convertToPresentableValue(String attr, CatalogEntry elem, List<FilterCriteria> includeCriteria, Instrospection instrospection) {
        Object value = nativeInterface.getWrappedValue(attr, instrospection, elem, false);
        if (value == null) {
            return null;
        } else if (nativeInterface.isCollection(value)) {
            return getUserReadableCollection(value, includeCriteria, instrospection);
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
    public void setAsPersistentValue(Object value, FieldDescriptor field, CatalogEntry jso, Instrospection instrospection) throws ReflectiveOperationException {

        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            access.deleteAttribute(jso, field.getFieldId(), instrospection);
        } else if (nativeInterface.isCollection(value)) {
            access.setPropertyValue(field, jso, nativeInterface.unwrapAsNativeCollection((Collection) value), instrospection);
        } else if (value instanceof Number) {

            // FIXME some runtimes (browser) may require to unwrap the java object
            /*Number v = (Number) value;
            if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {

                 access.setAttribute(jso, field, v.intValue(),instrospection);
            } else {
                access.setAttribute(jso, field, v.doubleValue(),instrospection);
            }
            */
            access.setPropertyValue(field, jso, value, instrospection);
        } else if (value instanceof String) {
            String v = (String) value;
            v = getSendableString(v);
            if (v == null) {
                access.deleteAttribute(jso, field.getFieldId(), instrospection);
            } else {
                if (field.isMultiple()) {
                    access.setPropertyValue(field, jso, nativeInterface.eval(v), instrospection);
                } else {
                    try {
                        switch (dataType) {
                            case CatalogEntry.BOOLEAN_DATA_TYPE:
                                access.parseSetBoolean(jso, field, v, instrospection);
                                break;
                            case CatalogEntry.INTEGER_DATA_TYPE:
                                if (field.isKey()) {
                                    //TODO distinguish client and server runtime cus KEYS ARE ALWAYS SUPPOSED TO BE STRINGS CLIENT-SIDE,
                                    access.setPropertyValue(field, jso, v, instrospection);
                                    //access.setAttribute(jso, fieldId, v);
                                } else {
                                    access.parseSetInteger(v, jso, field, instrospection);
                                }
                                break;
                            case CatalogEntry.NUMERIC_DATA_TYPE:
                                access.parseSetDouble(v, jso, field, instrospection);
                                break;
                            default:
                                access.setPropertyValue(field, jso, v, instrospection);
                        }
                    } catch (Exception e) {
                        access.setPropertyValue(field, jso, v, instrospection);
                        //access.setAttribute(jso, fieldId, v);
                    }
                }
            }

        } else {
            access.setPropertyValue(field, jso, value, instrospection);
            //access.setAttribute(jso, fieldId, value);
        }
    }

    @Override
    public Object convertToPersistentValue(Object value, FieldDescriptor field) {
        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            return null;
        } else if (nativeInterface.isCollection(value)) {
            return nativeInterface.unwrapAsNativeCollection((Collection) value);
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
            if (v == null && CatalogEntry.BOOLEAN_DATA_TYPE != dataType) {
                return null;
            }

            try {
                switch (dataType) {
                    case CatalogEntry.BOOLEAN_DATA_TYPE:
                        return "1".equals(v) || Boolean.parseBoolean(v);
                    case CatalogEntry.INTEGER_DATA_TYPE:
                        if (field.isKey()) {
                            return v;
                        }
                        return Integer.parseInt(v);
                    case CatalogEntry.NUMERIC_DATA_TYPE:
                        return Double.parseDouble(v);
                    //FIXME attempt to parse a date setRuntimeContext system date parser
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

