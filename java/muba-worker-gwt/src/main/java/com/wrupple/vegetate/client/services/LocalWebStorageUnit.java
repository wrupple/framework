package com.wrupple.vegetate.client.services;

import com.wrupple.muba.worker.shared.services.StorageManager.Unit;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface LocalWebStorageUnit extends Unit<CatalogEntry> {

	String UNIT = "client";

}
