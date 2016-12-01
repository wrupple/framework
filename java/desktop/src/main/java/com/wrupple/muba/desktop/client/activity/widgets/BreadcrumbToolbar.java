package com.wrupple.muba.desktop.client.activity.widgets;

import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.desktop.client.event.DesktopProcessEventHandler;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public interface BreadcrumbToolbar extends Toolbar ,DesktopProcessEventHandler{

	void setSize(int height);

	void onPlaceChange(DesktopPlace place, JsApplicationItem item);

}
