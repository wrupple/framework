package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogIntent;
import com.wrupple.muba.bootstrap.domain.reserved.*;

public interface ContentRevision extends HasStakeHolder,HasCatalogKey,Versioned,ContentNode {

	String CATALOG = "ContentRevision";

	String getValue();

}
