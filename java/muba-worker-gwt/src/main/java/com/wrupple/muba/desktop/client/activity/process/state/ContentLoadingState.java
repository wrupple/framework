package com.wrupple.muba.desktop.client.activity.process.state;

import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.client.activity.process.state.State;

public interface ContentLoadingState extends State.ContextAware<CatalogProcessDescriptor, JsCatalogEntry>{

}
