package com.wrupple.muba.cms.domain;

import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.Versioned;

public interface ContentRevision extends HasStakeHolder, HasCatalogId, HasEntryId,Versioned,ContentNode {

	String CATALOG = "ContentRevision";

	String getValue();

}
