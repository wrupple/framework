package com.wrupple.muba.desktop.client.activity;

import com.google.gwt.activity.shared.Activity;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;

public interface CatalogEntryImportActivity extends Activity {
    String[] ACTIVITY_ID = new String[]{CatalogActionRequest.CATALOG_ID_PARAMETER, "import"};
}
