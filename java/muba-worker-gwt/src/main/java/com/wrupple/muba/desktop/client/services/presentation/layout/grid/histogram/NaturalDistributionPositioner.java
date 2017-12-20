package com.wrupple.muba.desktop.client.services.presentation.layout.grid.histogram;

import com.wrupple.muba.desktop.client.services.presentation.layout.grid.AbstractRegularGridCellPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionser;
import com.wrupple.muba.desktop.domain.CellPosition;
import com.wrupple.vegetate.domain.HistogramModel;

public class NaturalDistributionPositioner extends
		AbstractRegularGridCellPositioner implements RegularGridCellPositionser {

	private int[][] matrix;
	private boolean cumulative;
	private HistogramModel model;
	

	@Override
	public CellPosition getPosition(int absoluteIndex) {
		int bucketIndex = -1;

		int frequency = -1;

		int matrixLength=matrix.length ;
		int matrixRowLength;
		int currentMatrixCellValue;
		for (int i = 0; i < matrixLength; i++) {
			matrixRowLength=matrix[i].length;
			for (int j = 0; j < matrixRowLength; j++) {
				currentMatrixCellValue=matrix[i][j];
				if (currentMatrixCellValue == absoluteIndex) {
					bucketIndex = i;
					frequency = j;
				}
			}
		}

		if (cumulative) {
			// only works if the collection was ordered incrementaly
			frequency = absoluteIndex;
		}

		if (bucketIndex == -1) {
			throw new IllegalArgumentException("Index " + absoluteIndex
					+ " was not found in histogram model");
		}

		int column = getColumn(bucketIndex, frequency);
		int row = getRow(bucketIndex, frequency);
		
		return super.getPosition(column, row);

	}

	private int getColumn(int bucketIndex, int frequency) {
		if(isGridIverted()){
			return getNumberOfRows() - bucketIndex;
		}
		return bucketIndex;
	}

	private int getRow(int bucketIndex, int frequency) {
		if(verticalFlip){
			return frequency;
		}
		int row = getNumberOfRows() - frequency;

		return row;
	}
	

	public boolean isCumulative() {
		return cumulative;
	}

	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}
	

	

	public int[][] getMatrix() {
		return matrix;
	}

	public void initializeHistogamGrid(HistogramModel model, int width, int height) {
		this.matrix = model.getMatrix();
		int totalCount = model.getElementCount();

		int highestFrequency = matrix[0].length;
		int numberOfBuckets = matrix.length;

		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i].length > highestFrequency) {
				highestFrequency = matrix[i].length;
			}
		}

		model.setHighestFrequency(highestFrequency);
		if(isGridIverted()){
			super.setNumberOfrows(numberOfBuckets);
			if(isCumulative()){
				super.setNumberOfColumns(totalCount);
			}else{
				super.setNumberOfColumns(highestFrequency);
			}
		}else{
			super.setNumberOfColumns(numberOfBuckets);
			if(isCumulative()){
				//cumulative histogram
				super.setNumberOfrows(totalCount);
			}else{
				super.setNumberOfrows(highestFrequency);
			}
		}
		double columnMaxWidth = width/super.getNumberOfColumns();
		double rowMaxHeight = height /super.getNumberOfRows();
		
		double cellsize = columnMaxWidth > rowMaxHeight ?  rowMaxHeight:columnMaxWidth;
		super.setColumnWidth(cellsize);
		super.setRowHeight(cellsize);
		this.model=model;
	}

	@Override
	public int getRulerBucketSizeInPixels() {
		return (int) super.getRowHeight();
	}

	@Override
	public double getRulerBucketValue() {
		return model.getIntervalWidth();
	}

	public HistogramModel getHistogramModel() {
		return model;
	}


}
