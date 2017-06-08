package com.wrupple.muba.bpm.shared.services.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


import java.util.Collection;

/**
 * Implemented from GWTFieldConversionStrategyImpl
 */
@Singleton
public class FieldConversionStrategyImpl implements FieldConversionStrategy {

    private final SystemCatalogPlugin access;
    private final ObjectNativeInterface nativeInterface;


    @Inject
    public FieldConversionStrategyImpl(SystemCatalogPlugin access, ObjectNativeInterface nativeInterface) {
        this.access = access;
        this.nativeInterface = nativeInterface;
    }


    @Override
    public Object convertToPresentableValue(String attr, CatalogEntry elem, List<FilterCriteria> includeCriteria, FieldAccessStrategy.Session session) {
        return access.userReadableValue(elem, attr, includeCriteria,session);
    }

    @Override
    public void setAsPersistentValue(Object value, FieldDescriptor field, CatalogEntry jso, FieldAccessStrategy.Session session) throws ReflectiveOperationException {

        int dataType = field == null ? -1 : field.getDataType();
        if (value == null) {
            access.deleteAttribute(jso, field.getFieldId(),session);
        } else if (nativeInterface.isCollection(value)) {
            access.setPropertyValue(field,jso,nativeInterface.unwrapAsNativeCollection((Collection) value),session);
        } else if (value instanceof Number) {
            Number v = (Number) value;
            if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {
                access.setAttribute(jso, field, v.intValue(),session);
            } else {
                access.setAttribute(jso, field, v.doubleValue(),session);
            }
        } else if (value instanceof String) {
            String v = (String) value;
            v = getSendableString(v);
            if (v == null) {
                access.deleteAttribute(jso, field.getFieldId(),session);
            }else{
                if(field.isMultiple()){
                    access.setPropertyValue(field,jso,nativeInterface.eval(v),session);
                }else{
                    try {
                        switch (dataType) {
                            case CatalogEntry.BOOLEAN_DATA_TYPE:
                                access.parseSetBoolean(jso, field, v,session);
                                break;
                            case CatalogEntry.INTEGER_DATA_TYPE:
                                if(field.isKey()){
                                    //TODO distinguish client and server runtime cus KEYS ARE ALWAYS SUPPOSED TO BE STRINGS CLIENT-SIDE,
                                    access.setPropertyValue(field,jso,v,session);
                                    //access.setAttribute(jso, fieldId, v);
                                }else{
                                    access.parseSetInteger(v,jso,field,session);
                                }
                                break;
                            case CatalogEntry.NUMERIC_DATA_TYPE:
                                access.parseSetDouble(v,jso,field,session);
                                break;
                            default:
                                access.setPropertyValue(field,jso,v,session);
                        }
                    } catch (Exception e) {
                        access.setPropertyValue(field,jso,v,session);
                        //access.setAttribute(jso, fieldId, v);
                    }
                }
            }

        } else {
            access.setPropertyValue(field,jso,value,session);
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

