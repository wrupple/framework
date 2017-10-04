package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasChildren;


public interface DomainRole extends HasChildren<Long> , CatalogEntry {
	public static final String CATALOG ="DomainRole";
}
