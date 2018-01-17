package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasChildren;


public interface DomainRole extends HasChildren<Long> , CatalogEntry {
    String CATALOG = "DomainRole";
}
