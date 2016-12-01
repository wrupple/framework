package com.wrupple.muba.desktop.client.services.presentation.layout;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.AbstractRegularGridCellPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionser;
import com.wrupple.muba.desktop.domain.CellPosition;

public class IndexedLayoutDelegateImpl implements IndexedLayoutDelegate {
	
	private CellPositioner positioner;
	private String cellWrapperClass;
	

	@Inject
	public IndexedLayoutDelegateImpl(CellPositioner positioner) {
		super();
		this.positioner = positioner;
	}
	
	
	

	@Override
	public void positionElement(LayoutPanel panel,Element container,Widget w, int absoluteIndex) {
		if(cellWrapperClass!=null){
			container.addClassName(cellWrapperClass);
		}
		CellPosition position = positioner.getPosition(absoluteIndex);
		panel.setWidgetLeftWidth(w, position.getX(), Unit.PX, position.getWidth(), Unit.PX);
		panel.setWidgetTopHeight(w, position.getY(), Unit.PX, position.getHeight(), Unit.PX);
	
	}


	@Override
	public void setCellWrapperClass(String cellWrapperClass) {
		this.cellWrapperClass=cellWrapperClass;
	}



	@Override
	public void initializePositions(List<?> values, int width, int height,
			int focusIndex) {
		positioner.setFocusIndex(focusIndex);
		positioner.setWidthAndHeight(width,height);
	}



	@Override
	public CellPositioner getCellPositioner() {
		return positioner;
	}
	

	/**
	 * @param pct the percentage of the space to actually use, and render this spaced centered
	 */
	public void setAutoMargin(String pct){
		if(pct==null){
			((RegularGridCellPositionser)positioner).setViewspace(-1,Unit.PX,-1);
		}else{
			double autoMargin;
			if(pct.startsWith("column")){
				double limit = Double.parseDouble(pct.substring(pct.indexOf(',')+1));
				((RegularGridCellPositionser)positioner).setViewspace(((AbstractRegularGridCellPositioner)positioner).getColumnWidth(), Unit.PX,limit);
				return;
			}else if(pct.endsWith("%")){
				autoMargin = Double.parseDouble(pct.substring(0, pct.length()-1));
				((RegularGridCellPositionser)positioner).setViewspace(autoMargin,Unit.PCT,autoMargin);
				return;
			}
			autoMargin = Double.parseDouble(pct);
			((RegularGridCellPositionser)positioner).setViewspace(autoMargin,Unit.PX,autoMargin);
		}
	}
	
	public void setCellSize(String cellSize){
		if(cellSize!=null){
			((RegularGridCellPositionser)positioner).setCellSize(Integer.parseInt(cellSize));
		}
		
	}

}
