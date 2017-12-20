package com.wrupple.vegetate.client.services;

import com.wrupple.vegetate.client.services.StorageManager.Unit;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface LocalWebStorageUnit extends Unit<CatalogEntry> {

	String UNIT = "client";

}
