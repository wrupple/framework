package com.wrupple.muba.desktop.client.services.logic;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionser;
import com.wrupple.vegetate.domain.HistogramModel;

public interface HistogramPositioner extends RegularGridCellPositionser {

	/**
	 * @param values values to draw in histogram
	 * @param width viewport width
	 * @param height viewport height
	 */
	void initializePositions(List<? extends JavaScriptObject> values,int width, int height,String numericField, boolean averageArrayValues);

	void setCumulative(boolean cumulative);
	
	HistogramModel getHistogramModel();

}
