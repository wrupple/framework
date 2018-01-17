package com.wrupple.muba.desktop.client.activity.process.state;

import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.client.activity.process.state.State;

import java.util.List;

public interface CatalogSelectionLoader extends State<DesktopPlace, List<JsCatalogEntry>> {

}
