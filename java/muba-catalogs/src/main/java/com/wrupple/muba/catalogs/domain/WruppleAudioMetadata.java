package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasBlobKey;

public interface WruppleAudioMetadata extends ContentNode, HasBlobKey {
	final String CATALOG = "AudioMetadata";
}
