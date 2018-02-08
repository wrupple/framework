package com.wrupple.vegetate.client.services;

import com.wrupple.muba.worker.shared.services.StorageManager.Unit;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface CreditCardStorageUnit extends Unit<CatalogEntry> {

	String UNIT = "cc";

}
