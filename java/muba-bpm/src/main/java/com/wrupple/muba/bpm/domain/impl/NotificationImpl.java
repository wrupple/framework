package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.Notification;

/**
 * Created by japi on 12/08/17.
 */
public class NotificationImpl extends ProcessRequestImpl implements Notification{
    private Long source;
    private String sourceType;
    private CatalogEntry sourcevalue;

    private Long target;
    private String targetType,outputCatalog;
    private CatalogEntry targetValue;

    @Override
    public CatalogEntry getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(CatalogEntry targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Override
    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public CatalogEntry getSourcevalue() {
        return sourcevalue;
    }

    public void setSourcevalue(CatalogEntry sourcevalue) {
        this.sourcevalue = sourcevalue;
    }

    @Override
    public String getSourceType() {
        return sourceType;
    }

    @Override
    public CatalogEntry getSourceValue() {
        return sourcevalue;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @Override
    public String getOutputCatalog() {
        return outputCatalog;
    }

    public void setOutputCatalog(String outputCatalog) {
        this.outputCatalog = outputCatalog;
    }
}


