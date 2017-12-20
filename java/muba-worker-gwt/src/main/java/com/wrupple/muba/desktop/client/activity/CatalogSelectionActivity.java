package com.wrupple.muba.desktop.client.activity;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;

public interface CatalogSelectionActivity extends CatalogActivity{
	String BROWSE_COMMAND ="select";

	String[] ACTIVITY_ID = new String[]{CatalogActionRequest.CATALOG_ID_PARAMETER,BROWSE_COMMAND};
}
