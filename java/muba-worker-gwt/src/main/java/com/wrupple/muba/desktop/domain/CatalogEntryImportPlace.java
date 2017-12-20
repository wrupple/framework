package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.CatalogEntryImportActivity;

@SuppressWarnings("serial")
public class CatalogEntryImportPlace extends DesktopPlace {

	public CatalogEntryImportPlace(String catalogid) {
		super(CatalogEntryImportActivity.ACTIVITY_ID);
		super.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, catalogid);
	}

}
