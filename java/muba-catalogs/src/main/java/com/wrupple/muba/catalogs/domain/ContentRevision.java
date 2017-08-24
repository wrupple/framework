package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.*;

public interface ContentRevision extends HasStakeHolder,HasCatalogKey,Versioned,ContentNode {

	String CATALOG = "ContentRevision";

	String getValue();

}
