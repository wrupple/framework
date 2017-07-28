package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildren;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.ContentNode;

import java.util.List;

/**
 * EntryId determines the input contract that created this context according to the data type referenced by the (HasCatalogId:)ApplicationItem.
 * The reference to this contract is found in the runtineContext
 *
 */
public interface ApplicationState extends HasDistinguishedName,ContentNode,HasEntryId,HasParent<ApplicationState>,HasChildren<ApplicationState> {

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

    // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);

}
