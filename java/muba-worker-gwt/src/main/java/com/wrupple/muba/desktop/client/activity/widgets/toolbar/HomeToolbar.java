package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.desktop.client.event.DesktopProcessEventHandler;
import com.wrupple.muba.desktop.client.event.VegetateEventHandler;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public interface HomeToolbar extends Toolbar,DesktopProcessEventHandler,VegetateEventHandler{

	void setSize(int height);

	void onPlaceChange(DesktopPlace place, JsApplicationItem item);

}
