package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.domain.ContentNode;

import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public interface ActivityContext extends UserContext,ContentNode,HasResult {
    final String CATALOG = "ActivityContext";

    Long getTaskDescriptor();

    ProcessTaskDescriptor getTaskDescriptorValue();

    public <T extends CatalogEntry> T getUserOutput();

    public <T extends CatalogEntry> List<T> getUserSelection();

    public boolean isCanceled();
}