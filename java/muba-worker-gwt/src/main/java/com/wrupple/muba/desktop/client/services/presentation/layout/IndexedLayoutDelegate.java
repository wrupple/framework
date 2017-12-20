package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface IndexedLayoutDelegate{

	CellPositioner getCellPositioner();
	
	void positionElement( LayoutPanel panel,Element container, Widget w, int absoluteIndex);

	void setCellWrapperClass(String cellWrapperClass);
	
	/**
	 * @param values values to draw in histogram
	 * @param width viewport width
	 * @param height viewport height
	 */
	void initializePositions(List<?> values,int width, int height,int focusIndex);

	
}
