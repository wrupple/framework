package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildren;


public interface DomainRole extends HasChildren<Long> , CatalogEntry {
	public static final String CATALOG ="DomainRole";
}
