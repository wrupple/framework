package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;

public interface HistogramLayoutDelegate extends IndexedLayoutDelegate {

	public void setNumericField(String numericField);
	
	public void setFlipVertical(String value);
	
	public void setGridIverted(String s);
	
	public void setCumulative(String value);

	public void setAverageNumericValues(String averageNumericValues) ;
}
