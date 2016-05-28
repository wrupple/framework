package com.wrupple.muba.bpm.domain;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.structure.HasChildren;


public interface DomainRole extends HasChildren<Long> , CatalogEntry {
	public static final String CATALOG ="DomainRole";
}
