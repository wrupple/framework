package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import org.apache.commons.chain.impl.ContextBase;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityContextImpl extends ContextBase implements ActivityContext {

    private Long id,image;
    private String  name;
    @NotNull
    private Long domain;
    private boolean anonymouslyVisible;
    private ProcessTaskDescriptor taskDescriptorValue;
    private boolean canceled;
    private List userSelectionValues;
    private Long taskDescriptor;


    @Override
    public String getCatalogType() {
        return ActivityContext.CATALOG;
    }

    public final Long getId() {
        return id;
    }

    public final void setId(Long catalogId) {
        this.id = catalogId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final Long getImage() {
        return image;
    }

    public final void setImage(Long image) {
        this.image = image;
    }

    public final Long getDomain() {
        return domain;
    }

    public final void setDomain(Long domain) {
        this.domain = domain;
    }

    public final boolean isAnonymouslyVisible() {
        return anonymouslyVisible;
    }

    public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
        this.anonymouslyVisible = anonymouslyVisible;
    }

    private CatalogEntry  result;

    @Override
    public <T extends CatalogEntry> T getConvertedResult() {
        return (T) result;
    }

    @Override
    public CatalogEntry getResult() {
        return result;
    }

    @Override
    public void setResult(CatalogEntry catalogEntry) {
        this.result=result;
    }

    private Date  timestamp;

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date d) {
    this.timestamp=timestamp;
    }

    private String distinguishedName;

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    private ExcecutionContext excecutionContex;

    @Override
    public ExcecutionContext getExcecutionContext() {
        return excecutionContex;
    }

    @Override
    public Long getTaskDescriptor() {
        return taskDescriptor;
    }

    @Override
    public ProcessTaskDescriptor getTaskDescriptorValue() {
        return taskDescriptorValue;
    }

    @Override
    public <T extends CatalogEntry> List<T> getUserSelectionValues() {
        return userSelectionValues;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setExcecutionContext(ExcecutionContext context) {
        this.excecutionContex=context;
    }

    @Override
    public void setTaskDescriptorValue(ProcessTaskDescriptor taskDescriptorValue) {
        this.taskDescriptorValue=taskDescriptorValue;

    }
}
