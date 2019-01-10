package com.wrupple.muba.worker.server.domain.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionConstraintImpl;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.worker.server.domain.ValueChangeTrigger;

import java.util.Date;
import java.util.List;

public class ValueChangeTriggerImpl extends CatalogActionConstraintImpl implements ValueChangeTrigger {
    @CatalogField(filterable = true)
    private String fieldId;
    private String initialValue;
    private String finalValue;
    private Integer encoding;
    private List<String> commandChain;
    @ForeignKey(foreignCatalog = Person.CATALOG)
    private Long stakeHolder;
    private Boolean runAsStakeHolder;
    private Boolean failSilence;
    private Boolean stopOnFail;
    private Date timestamp;

    @Override
    public String getDistinguishedName() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public String getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(String finalValue) {
        this.finalValue = finalValue;
    }

    @Override
    public Integer getEncoding() {
        return encoding;
    }

    public void setEncoding(Integer encoding) {
        this.encoding = encoding;
    }

    @Override
    public List<String> getCommandChain() {
        return commandChain;
    }

    public void setCommandChain(List<String> commandChain) {
        this.commandChain = commandChain;
    }

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    @Override
    public void setStakeHolder(Object stakeHolder) {
        setStakeHolder((Long)stakeHolder);
    }

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public Boolean getRunAsStakeHolder() {
        return runAsStakeHolder;
    }

    @Override
    public void setRunAsStakeHolder(Boolean runAsStakeHolder) {
        this.runAsStakeHolder = runAsStakeHolder;
    }

    @Override
    public Boolean getFailSilence() {
        return failSilence;
    }

    @Override
    public void setFailSilence(Boolean failSilence) {
        this.failSilence = failSilence;
    }

    @Override
    public Boolean getStopOnFail() {
        return stopOnFail;
    }

    @Override
    public void setStopOnFail(Boolean stopOnFail) {
        this.stopOnFail = stopOnFail;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }
}
