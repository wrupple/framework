package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptor;

import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class ApplicationStateImpl extends ManagedObjectImpl implements ApplicationState {

    private ServiceManifest handleValue;
    private FilterData filterData;
    private Long application;

    private Long taskDescriptor;
    private Long parent;
    private Object entry;


    private ProcessTaskDescriptor taskDescriptorValue;
    private List<Object> userSelection;
    private List<CatalogEntry> userSelectionValues;
    private String distinguishedName;
    private RuntimeContext excecutionContext;
    private CatalogDescriptor solutionDescriptor;
    private List<VariableDescriptor> solutionVariables;
    private CatalogEntry entryValue;
    private boolean canceled,draft;

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public boolean isDraft() {
        return draft;
    }

    @Override
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    @Override
    public void setTaskDescriptor(Object id) {
        this.setTaskDescriptor((Long)id);
    }

    private int taskIndex;

    private ApplicationState parentValue;

    @Override
    public ServiceManifest getHandleValue() {
        return handleValue;
    }

    public void setHandleValue(ServiceManifest handleValue) {
        this.handleValue = handleValue;
    }

    @Override
    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Override
    public Object getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object entry) {
        this.entry = entry;
    }

    @Override

    public FilterData getFilterData() {
        return filterData;
    }

    public void setFilterData(FilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    @Override
    public Long getTaskDescriptor() {
        return taskDescriptor;
    }

    public void setTaskDescriptor(Long taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }

    @Override
    public ProcessTaskDescriptor getTaskDescriptorValue() {
        return taskDescriptorValue;
    }

    @Override
    public void setTaskDescriptorValue(ProcessTaskDescriptor taskDescriptorValue) {
        this.taskDescriptorValue = taskDescriptorValue;
    }

    @Override
    public List<Object> getUserSelection() {
        return userSelection;
    }

    public void setUserSelection(List<Object> userSelection) {
        this.userSelection = userSelection;
    }

    public List<CatalogEntry> getUserSelectionValues() {
        return userSelectionValues;
    }

    public void setUserSelectionValues(List<CatalogEntry> userSelectionValues) {
        this.userSelectionValues = userSelectionValues;
    }

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public RuntimeContext getExcecutionContext() {
        return excecutionContext;
    }

    @Override
    public void setExcecutionContext(RuntimeContext excecutionContext) {
        this.excecutionContext = excecutionContext;
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
    public List<VariableDescriptor> getSolutionVariables() {
        return solutionVariables;
    }

    @Override
    public void setSolutionVariables(List<VariableDescriptor> solutionVariables) {
        this.solutionVariables = solutionVariables;
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
    public int getTaskIndex() {
        return taskIndex;
    }

    @Override
    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public ApplicationState getParentValue() {
        return parentValue;
    }

    @Override
    public ApplicationState getRootAncestor() {
        return CatalogEntryImpl.getRootAncestor(this);
    }

    public void setParentValue(ApplicationState parentValue) {
        this.parentValue = parentValue;
    }
}
