package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import org.apache.commons.chain.impl.ContextBase;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public class ApplicationContextImpl extends ContextBase implements ApplicationContext {

    private Long id,image;
    private String  name;
    @NotNull
    private Long domain;
    private boolean anonymouslyVisible;
    private ProcessTaskDescriptor taskDescriptorValue;
    private boolean canceled;
    private List userSelectionValues;
    private Long taskDescriptor;
    private CatalogDescriptor solutionDescriptor;
    private List<VariableDescriptor> solutionVariables;

    @Override
    public List<VariableDescriptor> getSolutionVariables() {
        return solutionVariables;
    }

    @Override
    public void setSolutionVariables(List<VariableDescriptor> solutionVariables) {
        this.solutionVariables = solutionVariables;
    }

    @Override
    public CatalogDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public void setSolutionDescriptor(CatalogDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    @Override
    public String getCatalogType() {
        return ApplicationContext.CATALOG;
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

    private RuntimeContext excecutionContex;

    @Override
    public RuntimeContext getRuntimeContext() {
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
    public void setExcecutionContext(RuntimeContext context) {
        this.excecutionContex=context;
    }

    @Override
    public void setTaskDescriptorValue(ProcessTaskDescriptor taskDescriptorValue) {
        this.taskDescriptorValue=taskDescriptorValue;

    }
}
