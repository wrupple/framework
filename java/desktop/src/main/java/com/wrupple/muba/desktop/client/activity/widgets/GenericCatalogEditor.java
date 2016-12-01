package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface GenericCatalogEditor extends CatalogEditor<JsCatalogEntry>,IsWidget{
	
	public void setBlock(String block);
	
	public void setLabelSource(String property);

	
}
