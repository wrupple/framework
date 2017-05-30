package com.wrupple.muba.bpm.shared.services.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bpm.shared.services.FieldAccessStrategy;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


import java.util.Collection;

@Singleton
public class FieldConversionStrategyImpl implements FieldConversionStrategy {

    private final FieldAccessStrategy access;

    @Inject
    public FieldConversionStrategyImpl(FieldAccessStrategy access) {
        this.access = access;
    }


    @Override
    public Object convertToUserReadableValue(String attr, CatalogEntry elem, List<FilterCriteria> includeCriteria) {
        return access.userReadableValue(elem, attr, includeCriteria);
    }

    @Override
    public void convertToPersistentDatabaseValue(Object value, FieldDescriptor field, CatalogEntry jso) {
        String fieldId = field.getFieldId();
        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            access.deleteAttribute(jso, fieldId);
        } else if (value instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) value;
            access.setAttribute(jso, fieldId, access.convertToJavaScriptArray(collection.toArray()));
        } else if (value instanceof Number) {
            Number v = (Number) value;
            if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {
                access.setAttribute(jso, fieldId, v.intValue());
            } else {
                access.setAttribute(jso, fieldId, v.doubleValue());
            }
        } else if (value instanceof String) {
            String v = (String) value;
            v = getSendableString(v);
            if (v == null) {
                access.deleteAttribute(jso, fieldId);
            }else{
                if(field.isMultiple()){
                    access.setAttribute(jso, fieldId, access.eval(v));
                }else{
                    try {
                        switch (dataType) {
                            case CatalogEntry.BOOLEAN_DATA_TYPE:
                                access.setAttribute(jso, fieldId, "1".equals(v) || Boolean.parseBoolean(v));
                                break;
                            case CatalogEntry.INTEGER_DATA_TYPE:
                                if(field.isKey()){
                                    //KEYS ARE ALWAYS SUPPOSED TO BE STRINGS CLIENT-SIDE
                                    access.setAttribute(jso, fieldId, v);
                                }else{
                                    access.parseSetInteger(v,jso,fieldId);
                                }
                                break;
                            case CatalogEntry.NUMERIC_DATA_TYPE:
                                access.parseSetDouble(v,jso,fieldId);
                                break;
                            default:
                                access.setAttribute(jso, fieldId, v);
                        }
                    } catch (Exception e) {
                        access.setAttribute(jso, fieldId, v);
                    }
                }
            }

        } else {

            access.setAttribute(jso, fieldId, value);

        }
    }

    @Override
    public Object convertToPersistentDatabaseValue(Object value, FieldDescriptor field) {
        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            return null;
        } else if (value instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) value;
            return access.convertToJavaScriptArray(collection.toArray());
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
            if (v == null) {
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
                    default:
                        return v;
                }
            } catch (Exception e) {
                return v;
            }

        } else if (access.isSystemObject(value)) {

            return value;

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

