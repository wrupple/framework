package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.HasBlobKey;
import com.wrupple.vegetate.domain.Versioned;

public interface WruppleFileMetadata extends ContentNode, HasBlobKey, Versioned {
	final String CATALOG = "DocumentMetadata";
}
