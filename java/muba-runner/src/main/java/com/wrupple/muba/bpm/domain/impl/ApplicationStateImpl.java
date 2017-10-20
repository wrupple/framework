package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class ApplicationStateImpl extends ManagedObjectImpl implements ApplicationState {

    @CatalogField(ignore = true)
    private ServiceManifest handleValue;
    @CatalogField(ignore = true)
    private FilterData filterData;

    @ForeignKey(foreignCatalog = Workflow.CATALOG)
    private Long handle;

    @ForeignKey(foreignCatalog = Task.CATALOG)
    private Long taskDescriptor;

    @ForeignKey(foreignCatalog = ApplicationState.CATALOG)
    private Long parent;
    @CatalogField(ignore = true)
    private ApplicationState parentValue;
    /*
     * keys must be stored in encoded format
     */
    private String entry;

    @CatalogField(ignore = true)
    private Task taskDescriptorValue;
    private List<String> userSelection;

    @CatalogField(ignore = true)
    private List<CatalogEntry> userSelectionValues;

    private String distinguishedName;


    private String catalog;

    @ForeignKey(foreignCatalog = CatalogDescriptor.CATALOG_ID)
    private Long solutionDescriptor;
    @CatalogField(ignore = true)
    private CatalogDescriptor catalogValue;
    @CatalogField(ignore = true)
    private List<VariableDescriptor> solutionVariablesValues;
    @CatalogField(ignore = true)
    private CatalogEntry entryValue;

    private Boolean canceled,draft;
    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getCatalogType() {
        return ApplicationState.CATALOG;
    }
    @Override
    public Boolean getCanceled() {
        return canceled==null?false:canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }
    public Long getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public void setSolutionDescriptor(Long solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }
    @Override
    public Boolean getDraft() {
        return draft==null?false:draft;
    }

    @Override
    public void setDraft(Boolean draft) {
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
        this.entry = (String) entry;
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
    public List<String> getUserSelection() {
        return userSelection;
    }

    public void setUserSelection(List<String> userSelection) {
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


    @Override
    public CatalogDescriptor getCatalogValue() {
        return catalogValue;
    }

    @Override
    public void setCatalogValue(CatalogDescriptor catalogValue) {
        this.catalogValue = catalogValue;
    }

    @Override
    public List<VariableDescriptor> getSolutionVariablesValues() {
        return solutionVariablesValues;
    }

    @Override
    public void setSolutionVariablesValues(List<VariableDescriptor> solutionVariablesValues) {
        this.solutionVariablesValues = solutionVariablesValues;
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

}
