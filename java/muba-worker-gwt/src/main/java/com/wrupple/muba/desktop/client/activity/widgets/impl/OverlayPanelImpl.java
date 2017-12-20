package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.wrupple.muba.desktop.client.activity.widgets.OverlayPanel;

public class OverlayPanelImpl extends LayoutPanel implements OverlayPanel {



	public OverlayPanelImpl() {
		super();
		getElement().getStyle().setProperty( "overflow", "auto");
	}

	@Override
	public void addAtPixelPosition(IsWidget w, int left, int top, int width, int height) {
		if(w.asWidget().getParent()!=this){
			add(w);
		}
		setWidgetPixelPosition(w,left,top,width,height);
	}
	
	@Override
	public void setWidgetPixelPosition(IsWidget w, int left, int top, int width, int height) {
		setWidgetLeftWidth(w, left, Unit.PX, width, Unit.PX);
		setWidgetTopHeight(w, top, Unit.PX, height, Unit.PX);
	}

	

}
