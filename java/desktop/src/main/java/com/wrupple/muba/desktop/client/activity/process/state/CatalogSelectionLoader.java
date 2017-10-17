package com.wrupple.muba.desktop.client.activity.process.state;

import java.util.List;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface CatalogSelectionLoader extends State<DesktopPlace, List<JsCatalogEntry>> {

}
