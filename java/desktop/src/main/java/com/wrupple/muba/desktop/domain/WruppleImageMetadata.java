package com.wrupple.muba.desktop.domain;

import java.util.Collection;

import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

public interface WruppleImageMetadata extends PersistentImageMetadata, CatalogKey {

	public String getName();

	public Collection<Long> getTags();

	public String getBlobKey();
}
