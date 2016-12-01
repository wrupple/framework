package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.logic.HistogramLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.HistogramPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegateImpl;

public class HistogramLayoutDelegateImpl extends IndexedLayoutDelegateImpl
		implements HistogramLayoutDelegate {

	private HistogramPositioner elementPositioner;
	private String numericField;
	private boolean averageNumericValues;

	@Inject
	public HistogramLayoutDelegateImpl(HistogramPositioner positioner) {
		super(positioner);
		this.elementPositioner = positioner;
	}

	@Override
	public void initializePositions(List<?> values, int width, int height,int focusIndex) {
		super.initializePositions(values, width, height, focusIndex);
		elementPositioner.initializePositions((List<? extends JavaScriptObject>) values, width, height,numericField, averageNumericValues);
	}

	public void setNumericField(String numericField) {
		this.numericField = numericField;
	}
	
	public void setFlipVertical(String value){
		if (value == null) {
			elementPositioner.setFlipVertically(false);
		} else {
			elementPositioner.setFlipVertically(true);
		}
	}
	
	
	public void setCumulative(String value){
		if (value == null) {
			elementPositioner.setCumulative(false);
		} else {
			elementPositioner.setCumulative(true);
		}
	}


	public void setAverageNumericValues(String averageNumericValues) {
		if (averageNumericValues == null) {
			this.averageNumericValues = false;
		} else {
			this.averageNumericValues = true;
		}
	}

	@Override
	public void setGridIverted(String value) {
		if (value == null) {
			elementPositioner.setGridIverted(false);
		} else {
			elementPositioner.setGridIverted(true);
		}
	}
	
	

}
