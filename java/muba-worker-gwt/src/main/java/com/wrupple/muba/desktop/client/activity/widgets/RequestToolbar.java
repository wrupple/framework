package com.wrupple.muba.desktop.client.activity.widgets;

import com.wrupple.muba.desktop.client.event.VegetateEventHandler;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public interface RequestToolbar extends Toolbar, VegetateEventHandler{

	void setSize(int height);

}
