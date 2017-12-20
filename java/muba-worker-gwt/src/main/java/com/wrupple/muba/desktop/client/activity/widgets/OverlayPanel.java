package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.IsWidget;

public interface OverlayPanel extends IsWidget{

	void addAtPixelPosition(IsWidget w, int left, int top, int width, int height); 
	
	void setWidgetPixelPosition(IsWidget w, int left, int top, int width, int height) ;
}
