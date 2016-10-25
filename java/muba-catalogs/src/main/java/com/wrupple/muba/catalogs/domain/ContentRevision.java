package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bootstrap.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.ContentNode;

public interface ContentRevision extends HasStakeHolder, HasCatalogId, HasEntryId,Versioned,ContentNode {

	String CATALOG = "ContentRevision";

	String getValue();

}
