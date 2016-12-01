package com.wrupple.muba.desktop.client.activity;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;


public interface CatalogEntryCreateActivity extends  CatalogActivity{
	String ACTIVITY_ID = CatalogActionRequest.CATALOG_ID_PARAMETER + "/" + CatalogActionRequest.CREATE_ACTION;
}
