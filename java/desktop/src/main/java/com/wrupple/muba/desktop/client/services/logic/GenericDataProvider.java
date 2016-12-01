package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface GenericDataProvider extends
		FilterableDataProvider<JsCatalogEntry>  {

	void setCustomJoins(String[][] customJoins);

	void setUseCache(boolean b);

}
