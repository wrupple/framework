package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

import java.util.Collection;
import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class ApplicationStateImpl extends ManagedObjectImpl implements ApplicationState {

    private CatalogDescriptor solutionDescriptor;

    private Long parent, entry, taskDescriptor;
    private CatalogEntry parentValue,entryValue;
    private String distinguishedName;
    private List<Long> children;
    private FilterData filterData;
    private ProcessTaskDescriptor taskDescriptorValue;
    private List<CatalogEntry> userSelectionValues;
    private List<VariableDescriptor> solutionVariables;
    private boolean canceled;

    @Override
    public CatalogDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public void setSolutionDescriptor(CatalogDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    @Override
    public Long getParent() {
        return parent;
    }

    @Override
    public Long spawnChild() {
        return null;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Override
    public Long getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object id) {
        this.entry= (Long) id;
    }

    public void setEntry(Long entry) {
        this.entry = entry;
    }

    @Override
    public Long getTaskDescriptor() {
        return taskDescriptor;
    }

    public void setTaskDescriptor(Long taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }

    public CatalogEntry getParentValue() {
        return parentValue;
    }

    public void setParentValue(CatalogEntry parentValue) {
        this.parentValue = parentValue;
    }

    @Override
    public CatalogEntry getEntryValue() {
        return entryValue;
    }

    @Override
    public void setEntryValue(CatalogEntry entryValue) {
        this.entryValue = entryValue;
    }

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    @Override
    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    @Override
    public FilterData getFilterData() {
        return filterData;
    }

    public void setFilterData(FilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public ProcessTaskDescriptor getTaskDescriptorValue() {
        return taskDescriptorValue;
    }

    @Override
    public void setTaskDescriptorValue(ProcessTaskDescriptor taskDescriptorValue) {
        this.taskDescriptorValue = taskDescriptorValue;
    }

    public List<CatalogEntry> getUserSelectionValues() {
        return userSelectionValues;
    }

    public void setUserSelectionValues(List<CatalogEntry> userSelectionValues) {
        this.userSelectionValues = userSelectionValues;
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
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setExcecutionContext(RuntimeContext context) {

    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
