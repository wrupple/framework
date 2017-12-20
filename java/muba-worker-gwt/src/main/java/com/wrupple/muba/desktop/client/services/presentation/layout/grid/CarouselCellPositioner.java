package com.wrupple.muba.desktop.client.services.presentation.layout.grid;

import com.wrupple.muba.desktop.client.services.presentation.layout.CellPositioner;
import com.wrupple.muba.desktop.domain.CellPosition;

public class CarouselCellPositioner implements CellPositioner {

	private int focusIndex;
	private int viewportWidth;
	private int viewportHeight;
	
	public CarouselCellPositioner(int viewportWidth, int viewportHeight) {
		super();
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
	}

	@Override
	public CellPosition getPosition(int absoluteIndex) {
		if(absoluteIndex==focusIndex){
			return new CellPosition(0, 0, viewportWidth, viewportHeight);
		}else{
			if(absoluteIndex<focusIndex){
				return new CellPosition(-viewportWidth,0 , viewportWidth, viewportHeight);
			}else{
				return new CellPosition(viewportWidth*2,0 , viewportWidth, viewportHeight);
			}
		}
	}

	@Override
	public void setFocusIndex(int focusIndex) {
		this.focusIndex=focusIndex;
	}

	@Override
	public double getRulerBucketValue() {
		return 1;
	}

	@Override
	public int getRulerBucketSizeInPixels() {
		return viewportHeight;
	}

	@Override
	public int getViewPortHeight() {
		return viewportHeight;
	}

	@Override
	public int getViewPortWidth() {
		return viewportWidth;
	}

	@Override
	public void setWidthAndHeight(int width, int height) {
		this.viewportWidth=width;
		this.viewportHeight=height;
	}

}
