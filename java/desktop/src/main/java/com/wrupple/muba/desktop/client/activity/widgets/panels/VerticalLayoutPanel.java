package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class VerticalLayoutPanel extends ResizeComposite {

	private LayoutPanel main;

	public VerticalLayoutPanel() {
		super();
		main = new LayoutPanel();
		initWidget(main);
	}
	
	public void add(IsWidget widget){
		main.add(widget);
		
		//redraw
		double count = main.getWidgetCount();
		double pct = 100d/count;
		Unit unit = Unit.PCT;
		Widget w;
		double top;
		for(int i = 0 ; i < count; i++){
			w = main.getWidget(i);
			top =  pct * i;
			main.setWidgetTopHeight(w, top, unit, pct, unit);
		}
	}
	
	public LayoutPanel getUnderlyingPanel(){
		return main;
	}
	
}
