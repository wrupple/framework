package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasBlobKey;
import com.wrupple.muba.event.domain.reserved.Versioned;

public interface WruppleFileMetadata extends ContentNode, HasBlobKey, Versioned {
	final String CATALOG = "DocumentMetadata";
}
