package com.wrupple.muba.desktop.client.activity.widgets;

import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.desktop.client.event.VegetateEventHandler;

public interface RequestToolbar extends Toolbar, VegetateEventHandler{

	void setSize(int height);

}
