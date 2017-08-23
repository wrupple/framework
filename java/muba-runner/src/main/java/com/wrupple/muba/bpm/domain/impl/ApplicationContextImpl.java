package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public class ApplicationContextImpl extends ContextBase implements ApplicationContext {

    private final EventRegistry eventRegistry;

    private Long id,image;
    private String  name;
    @NotNull
    private Long domain;
    private Long stakeHolder;
    private boolean anonymouslyVisible;
    private ProcessTaskDescriptor taskDescriptorValue;
    private boolean canceled;
    private List userSelectionValues;
    private List<Long> children;
    private Long taskDescriptor,entry;
    private CatalogEntry entryValue;
    private Long parent;
    private int taskIndex;
    private CatalogDescriptor solutionDescriptor;
    private List<VariableDescriptor> solutionVariables;
    private FilterData filterData;

    @Inject
    public ApplicationContextImpl(EventRegistry eventRegistry) {
        this.eventRegistry = eventRegistry;
    }

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

    @Override
    public EventRegistry getServiceBus() {
        return eventRegistry;
    }

    @Override
    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    @Override
    public Long getParent() {
        return parent;
    }

    @Override
    public Long spawnChild() {
        throw new IllegalArgumentException("process switching has not been implemented");
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Override
    public Long getEntry() {
        return entry;
    }

    public void setEntry(Object entry) {
        this.entry = (Long) entry;
    }

    public CatalogEntry getEntryValue() {
        return entryValue;
    }

    @Override
    public void setEntryValue(CatalogEntry entryValue) {
        this.entryValue = entryValue;
    }

    @Override
    public FilterData getFilterData() {
        return filterData;
    }

    public void setFilterData(FilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    @Override
    public void setStakeHolder(long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }
}
