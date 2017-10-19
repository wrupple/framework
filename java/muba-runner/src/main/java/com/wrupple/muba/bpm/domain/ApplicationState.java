package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.*;

import java.util.List;

/**
 * EntryId determines the input contract that created this context according to the data type referenced by the (HasCatalogId:)Workflow.
 * The reference to this contract is found in the runtineContext
 *
 */
public interface ApplicationState extends HasDistinguishedName,ManagedObject,HasEntryId,HasParentValue<Long,ApplicationState>{

     String CATALOG = "ApplicationState";

     void setSession(Object sessionid);

    ServiceManifest getHandleValue();
    void setHandleValue(ServiceManifest applicationItemValue);

     FilterData getFilterData();

     Long getHandle();

    Long getTaskDescriptor();

    Task getTaskDescriptorValue();

    List<String> getUserSelection();

     <T extends CatalogEntry> List<T> getUserSelectionValues();

     Boolean getCanceled();

    void setTaskDescriptorValue(Task request);

    void setSolutionDescriptorValue(CatalogDescriptor solutionDescriptorValue);

    CatalogDescriptor getSolutionDescriptorValue();

    void setSolutionVariablesValues(List<VariableDescriptor> variables);

    List<VariableDescriptor> getSolutionVariablesValues();

    void setEntryValue(CatalogEntry booking);

    CatalogEntry getEntryValue();

    /**
     *
     * TODO client manages draft state applicationState.setDraft(false);
     * TODO If entry is a draft delete it when application state is deleted
     * return staging status
     */
    Boolean getDraft();

    void setDraft(Boolean b);

    void setTaskDescriptor(Object id);

    // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);



}
