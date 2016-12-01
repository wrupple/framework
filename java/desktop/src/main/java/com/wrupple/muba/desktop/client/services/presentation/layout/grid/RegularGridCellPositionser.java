package com.wrupple.muba.desktop.client.services.presentation.layout.grid;

import com.google.gwt.dom.client.Style.Unit;
import com.wrupple.muba.desktop.client.services.presentation.layout.CellPositioner;
import com.wrupple.muba.desktop.domain.CellPosition;


public interface RegularGridCellPositionser  extends CellPositioner{
	
	CellPosition getPosition(int column, int row);
	
	public void setGridIverted(boolean gridIverted);

	void setFlipVertically(boolean verticalFlip);

	void setViewspace(double autoMargin,Unit unit, double limit);

	void setCellSize(int cellSize);
}
