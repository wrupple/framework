package com.wrupple.muba.desktop.client.services.presentation.layout.grid;

import com.google.gwt.dom.client.Style.Unit;
import com.wrupple.muba.desktop.domain.CellPosition;

public abstract class AbstractRegularGridCellPositioner implements RegularGridCellPositionser {

	protected double rowHeight;
	protected double columnWidth;
	protected boolean verticalFlip;
	private int focusIndex;
	private int numberOfColumns;
	private int numberOfrows;

	private boolean gridIverted;
	private int width = -1;
	private double desiredWidth = -1;
	private int startingX = -1;
	private double autoMargin;
	private Unit unit;
	private double limit;

	@Override
	public CellPosition getPosition(int absoluteIndex) {
		if (startingX < 0) {
			// not initialized
			if (desiredWidth > 0 && width > 0) {
				// allows liquid layouts
				if (desiredWidth > width) {
					desiredWidth = width;
				}

				numberOfColumns = (int) (desiredWidth / columnWidth);

				double usedWidth = numberOfColumns * columnWidth;
				double availableMargin = width - usedWidth;
				startingX = (int) (availableMargin / 2);
				if (startingX < 0) {
					// IT COULD HAPPEND! .... kinda
					startingX = 0;
				}
			}
		}

		int column = absoluteIndex % numberOfColumns;
		int row = absoluteIndex / numberOfColumns;
		CellPosition position = getPosition(column, row);
		return position;
	}

	@Override
	public CellPosition getPosition(int column, int row) {
		/*
		 * if(verticalFlip){ row = numberOfrows-row; }
		 */
		double x, y;
		if (gridIverted) {
			x = row * columnWidth;
			y = column * rowHeight;
		} else {
			x = column * columnWidth;
			y = row * rowHeight;
		}
		if (startingX > 0) {
			x = x + startingX;
		}
		return new CellPosition(x, y, columnWidth, rowHeight);
	}

	@Override
	public void setViewspace(double autoMargin, Unit unit, double limit) {
		if (autoMargin <= 1) {
			return;
		}
		if (unit == null) {
			unit = Unit.PX;
		}
		if (columnWidth <= 0) {
			columnWidth = 1;
		}
		this.unit = unit;
		if (Unit.PCT == unit) {

			if (autoMargin > 100) {
				autoMargin = 100;
			}
			autoMargin=autoMargin/100;
		}
		this.limit = limit;
		this.autoMargin = autoMargin;

	}

	@Override
	public void setWidthAndHeight(int width, int height) {
		this.width = width;
		if (unit != null && autoMargin > 0) {
			if (Unit.PCT == unit) {

				this.numberOfColumns = (int) ((autoMargin * width) / columnWidth);
				if (this.numberOfColumns <= 0) {
					this.numberOfColumns = 1;
				}
				this.desiredWidth = numberOfColumns * columnWidth;

			} else {
				this.numberOfColumns = (int) (autoMargin / columnWidth);
				if (this.numberOfColumns <= 0) {
					this.numberOfColumns = 1;
				}
				
				// set number of px
				this.desiredWidth = numberOfColumns * columnWidth;
				//reiterate?
				if ( limit > autoMargin && this.desiredWidth < width) {
					if(width>limit){
						this.autoMargin=limit;
					}else{
						this.autoMargin=width;
					}
					setWidthAndHeight(width, height);
					return;
				}
			}
		}
		if (desiredWidth > 0) {
			startingX = (int) ((width - desiredWidth) / 2);
		}
	}

	@Override
	public int getViewPortHeight() {
		// TODO this implementation, due to NaturalDistribution usage, does not
		// make sense
		if (gridIverted) {
			return (int) ((numberOfrows + 1) * rowHeight);
		} else {
			return (int) ((numberOfColumns + 1) * columnWidth);
		}
	}

	@Override
	public int getViewPortWidth() {
		// TODO this implementation, due to NaturalDistribution usage, does not
		// make sense

		if (gridIverted) {
			return (int) ((numberOfColumns + 1) * columnWidth);
		} else {
			return (int) ((numberOfrows + 1) * rowHeight);
		}
	}

	public double getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(double rowHeight) {
		this.rowHeight = rowHeight;
	}

	public double getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(double columnWidth) {
		this.columnWidth = columnWidth;
	}

	@Override
	public void setCellSize(int cellSize) {
		setColumnWidth(cellSize);
		setRowHeight(cellSize);
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public int getNumberOfRows() {
		return numberOfrows;
	}

	public void setNumberOfrows(int numberOfrows) {
		this.numberOfrows = numberOfrows;
	}

	public boolean isGridIverted() {
		return gridIverted;
	}

	public void setGridIverted(boolean gridIverted) {
		this.gridIverted = gridIverted;
	}

	public int getFocusIndex() {
		return focusIndex;
	}

	public void setFocusIndex(int focusIndex) {
		this.focusIndex = focusIndex;
	}

	public int getNumberOfrows() {
		return numberOfrows;
	}

	public void setFlipVertically(boolean verticalFlip) {
		this.verticalFlip = verticalFlip;
	}

}
