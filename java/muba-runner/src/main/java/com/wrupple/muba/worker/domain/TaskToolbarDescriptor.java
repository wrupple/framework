package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface TaskToolbarDescriptor extends HasProperties, CatalogEntry {

    String CATALOG = "ToolbarDescriptor";
    String TYPE_FIELD = "type";

    String getType();


    Number getTask();

}
