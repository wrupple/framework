package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegateImpl;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionerImpl;

public class LayoutGrid extends PanelWithLayoutPositioner {


	public LayoutGrid() {
		this(4, 126, 126);
	}

	public LayoutGrid(int columns, int HeightInPixels, int WidthInPixels) {
		super(new IndexedLayoutDelegateImpl(new RegularGridCellPositionerImpl(columns, HeightInPixels, WidthInPixels)));
		setOverflow("auto");
	}

	@Override
	protected void onLoad() {
		int totalWidth = GWTUtils.getNonZeroParentWidth(this);
		
		for (Widget appicon : panel) {
			panel.setWidgetTopHeight(appicon, -100, Unit.PX, 50, Unit.PX);
			panel.setWidgetLeftWidth(appicon, totalWidth / 2, Unit.PX, 50, Unit.PX);
		}
		panel.forceLayout();
		int count = 0;
		for (Widget appicon : panel) {
			super.positionElement(appicon, count);
			count++;
		}
		panel.animate(1000);
		panel.addStyleName("wrupple-layout-grid");
	}

	public void add(Widget w){
		panel.add(w);
	}

	public void positionElement(IsWidget w) {
		super.positionElement(w, panel.getWidgetIndex(w));
	}

	public void forceLayout() {
		panel.forceLayout();
	}

	public void animate(int duration) {
		panel.animate(duration);
	}

	

}
