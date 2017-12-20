package com.wrupple.muba.desktop.client.activity;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;

public interface CatalogEntryReadActivity extends CatalogActivity{
	String ACTIVITY_ID = CatalogActionRequest.CATALOG_ID_PARAMETER + "/" + CatalogActionRequest.READ_ACTION;
}
