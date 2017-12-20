package com.wrupple.muba.desktop.client.services.presentation.layout.grid;

import com.google.inject.Inject;

public class RegularGridCellPositionerImpl extends AbstractRegularGridCellPositioner {

	public RegularGridCellPositionerImpl(int numberOfColumns, double rowHeight, double columnWidth) {
		super.setColumnWidth(columnWidth);
		super.setNumberOfColumns(numberOfColumns);
		super.setRowHeight(rowHeight);
	}

	@Inject
	public RegularGridCellPositionerImpl() {
		this(4,175, 175);
	}

	@Override
	public int getRulerBucketSizeInPixels() {
		return (int) super.getRowHeight();
	}

	@Override
	public double getRulerBucketValue() {
		return 1;
	}


}
