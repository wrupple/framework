package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.wrupple.muba.desktop.domain.CellPosition;

public interface CellPositioner {

	CellPosition getPosition(int absoluteIndex);
	
	void setFocusIndex(int focusIndex);

	int getRulerBucketSizeInPixels();

	double getRulerBucketValue();

	int getViewPortHeight();

	int getViewPortWidth();

	void setWidthAndHeight(int width, int height);

	
}
