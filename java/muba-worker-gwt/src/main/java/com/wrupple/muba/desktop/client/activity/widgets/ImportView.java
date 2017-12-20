package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface ImportView extends IsWidget {
	
	String getCsv();

	void addActions(List<Image> actions);
}
