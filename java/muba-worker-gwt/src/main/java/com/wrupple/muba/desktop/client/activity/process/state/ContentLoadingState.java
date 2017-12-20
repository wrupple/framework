package com.wrupple.muba.desktop.client.activity.process.state;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface ContentLoadingState extends State.ContextAware<CatalogProcessDescriptor, JsCatalogEntry>{

}
