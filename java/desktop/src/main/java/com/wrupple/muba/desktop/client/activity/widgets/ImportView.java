package com.wrupple.muba.desktop.client.activity.widgets;

import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

public interface ImportView extends IsWidget {
	
	String getCsv();

	void addActions(List<Image> actions);
}
