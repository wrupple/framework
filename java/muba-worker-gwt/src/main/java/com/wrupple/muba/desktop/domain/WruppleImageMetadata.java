package com.wrupple.muba.desktop.domain;

import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

import java.util.Collection;

public interface WruppleImageMetadata extends PersistentImageMetadata, CatalogKey {

    String getName();

    Collection<Long> getTags();

    String getBlobKey();
}
