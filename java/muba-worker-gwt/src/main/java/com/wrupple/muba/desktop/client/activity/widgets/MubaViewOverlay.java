package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface MubaViewOverlay extends HasWidgets {
	
	/**
	 * overlays the given item above the contents of the view at the given position.
	 * 
	 * @param element
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	void overlay(IsWidget element, int x,int y, int width, int height);
	
	void menu(Element element, List<Widget> options);
	
	void menu(int x,int y, List<Widget> options);
	
	void subMenu(Widget parentOption, List<Widget> options);
	
}
