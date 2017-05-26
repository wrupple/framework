package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.ContentNode;

import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public interface ActivityContext extends UserContext,ContentNode,HasDistinguishedName {
    final String CATALOG = "ActivityContext";

    Long getTaskDescriptor();

    ProcessTaskDescriptor getTaskDescriptorValue();

    public <T extends CatalogEntry> List<T> getUserSelectionValues();

    public boolean isCanceled();

    void setExcecutionContext(ExcecutionContext context);

    void setTaskDescriptorValue(ProcessTaskDescriptor request);

    void setSolutionDescriptor(CatalogDescriptor solutionDescriptor);

    CatalogDescriptor getSolutionDescriptor();

    void setSolutionVariables(List<VariableDescriptor> variables);

    List<VariableDescriptor> getSolutionVariables();

   // public void setLayoutUnit(String s);

    //public void setTransactionViewClass(String s);

   // void setSaveTo(String task.getProducedField());
}