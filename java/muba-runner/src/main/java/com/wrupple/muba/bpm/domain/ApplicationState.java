package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.reserved.*;
import com.wrupple.muba.event.domain.CatalogDescriptor;

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

     Long getApplication();

    Long getTaskDescriptor();

    ProcessTaskDescriptor getTaskDescriptorValue();

    List<Object> getUserSelection();

     <T extends CatalogEntry> List<T> getUserSelectionValues();

     boolean isCanceled();

    void setExcecutionContext(RuntimeContext context);

    void setTaskDescriptorValue(ProcessTaskDescriptor request);

    void setSolutionDescriptor(CatalogDescriptor solutionDescriptor);

    CatalogDescriptor getSolutionDescriptor();

    void setSolutionVariables(List<VariableDescriptor> variables);

    List<VariableDescriptor> getSolutionVariables();

    void setEntryValue(CatalogEntry booking);

    CatalogEntry getEntryValue();

    /**
     *
     * TODO client manages draft state applicationState.setDraft(false);
     * TODO If entry is a draft delete it when application state is deleted
     * return staging status
     */
    boolean isDraft();

    void setDraft(boolean b);

    void setTaskDescriptor(Object id);

    // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);



    int getTaskIndex();

    void setTaskIndex(int index);

}
