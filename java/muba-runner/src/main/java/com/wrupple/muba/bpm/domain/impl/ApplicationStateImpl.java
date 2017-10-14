package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.VariableDescriptor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class ApplicationStateImpl extends ManagedObjectImpl implements ApplicationState {

    @NotNull
    private ServiceManifest handleValue;
    private FilterData filterData;
    private Long handle;

    private Long taskDescriptor;
    private Long parent;

    private ApplicationState parentValue;

    private Object entry;
    private Object session;


    private Task taskDescriptorValue;
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
    public Long getHandle() {
        return handle;
    }

    public void setHandle(Long handle) {
        this.handle = handle;
    }

    @Override
    public Long getTaskDescriptor() {
        return taskDescriptor;
    }

    public void setTaskDescriptor(Long taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }

    @Override
    public Task getTaskDescriptorValue() {
        return taskDescriptorValue;
    }

    @Override
    public void setTaskDescriptorValue(Task taskDescriptorValue) {
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

    public Object getSession() {
        return session;
    }

    @Override
    public void setSession(Object session) {
        this.session = session;
    }
}
