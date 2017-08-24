package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.reserved.HasChildren;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.reserved.HasParent;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

import java.util.List;

/**
 * EntryId determines the input contract that created this context according to the data type referenced by the (HasCatalogId:)ApplicationItem.
 * The reference to this contract is found in the runtineContext
 *
 */
public interface ApplicationState extends HasDistinguishedName,ManagedObject,HasEntryId,HasParent<Long>,HasChildren<Long> {

     String CATALOG = "ApplicationState";

    public FilterData getFilterData();

    Long getTaskDescriptor();

    ProcessTaskDescriptor getTaskDescriptorValue();

    public <T extends CatalogEntry> List<T> getUserSelectionValues();

    public boolean isCanceled();

    void setExcecutionContext(RuntimeContext context);

    void setTaskDescriptorValue(ProcessTaskDescriptor request);

    void setSolutionDescriptor(CatalogDescriptor solutionDescriptor);

    CatalogDescriptor getSolutionDescriptor();

    void setSolutionVariables(List<VariableDescriptor> variables);

    List<VariableDescriptor> getSolutionVariables();

    void setEntryValue(CatalogEntry booking);

    CatalogEntry getEntryValue();

    // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);

}
