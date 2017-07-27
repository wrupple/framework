package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildren;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.ContentNode;

import java.util.List;

public interface ApplicationState extends HasDistinguishedName,ContentNode,HasParent<ApplicationState>,HasChildren<ApplicationState> {


    String getExitActivity();

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

    // void setSaveTo(String task.getProducedField());
}
