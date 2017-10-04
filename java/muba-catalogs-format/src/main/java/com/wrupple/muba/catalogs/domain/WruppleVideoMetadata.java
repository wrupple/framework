package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasBlobKey;

public interface WruppleVideoMetadata extends ContentNode, HasBlobKey {
	final String CATALOG = "VideoMetadata";

}
