package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.wrupple.muba.desktop.client.event.DesktopProcessEventHandler;
import com.wrupple.muba.desktop.client.event.VegetateEventHandler;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public interface HomeToolbar extends Toolbar,DesktopProcessEventHandler,VegetateEventHandler{

	void setSize(int height);

	void onPlaceChange(DesktopPlace place, JsApplicationItem item);

}
