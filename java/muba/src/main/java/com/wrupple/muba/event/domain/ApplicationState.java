package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.reserved.HasParentValue;

import java.util.List;

/**
 * EntryId determines the input contract that created this context according to the data type referenced by the (HasCatalogId:)Workflow.
 * The reference to this contract is found in the runtineContext
 */
public interface ApplicationState extends HasDistinguishedName, ManagedObject, HasEntryId, HasParentValue<Long, ApplicationState>, Event {

    String CATALOG = "ApplicationState";

    WorkerState getWorkerStateValue();

    Task getTaskDescriptorValue();

    Application getApplicationValue();

    void setApplicationValue(Application applicationItemValue);

    FilterData getFilterData();

    Long getApplication();

    Long getTaskDescriptor();

    void setTaskDescriptor(Object id);

    void setTaskDescriptorValue(Task request);

    List<String> getUserSelection();

    <T extends CatalogEntry> List<T> getUserSelectionValues();

    Boolean getCanceled();

    CatalogDescriptor getCatalogValue();

    void setCatalogValue(CatalogDescriptor catalogValue);

    List<VariableDescriptor> getSolutionVariablesValues();

    void setSolutionVariablesValues(List<VariableDescriptor> variables);

    CatalogEntry getEntryValue();

    void setEntryValue(CatalogEntry booking);

    /**
     * TODO client manages draft state applicationState.setDraft(false);
     * TODO If entry is a draft delete it when application state is deleted
     * return staging status
     */
    Boolean getDraft();

    void setDraft(Boolean b);

    void setApplication(Object id);

    void setWorkerStateValue(WorkerState workerState);

    void setUserSelection(List<String> userSelection);

    // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);


}
