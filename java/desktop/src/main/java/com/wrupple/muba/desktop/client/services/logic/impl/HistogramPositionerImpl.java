package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.logic.HistogramDataExtractor;
import com.wrupple.muba.desktop.client.services.logic.HistogramPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.histogram.NaturalDistributionPositioner;
import com.wrupple.muba.desktop.shared.services.BucketSplittingStrategy;
import com.wrupple.muba.desktop.shared.services.HistogramDataProcessor;
import com.wrupple.vegetate.domain.HistogramModel;

public class HistogramPositionerImpl extends NaturalDistributionPositioner implements HistogramPositioner {
	
	HistogramDataProcessor processor;
	HistogramDataExtractor extractor;
	BucketSplittingStrategy divider;

	@Inject
	public HistogramPositionerImpl(HistogramDataProcessor processor,
			HistogramDataExtractor extractor,
			BucketSplittingStrategy divider) {
		super();
		this.processor = processor;
		this.extractor = extractor;
		this.divider = divider;
		setCumulative(false);
	}
	

	@Override
	public void initializePositions(List<? extends JavaScriptObject> values,int width,int height,String numericField, boolean averageArrayValues) {
		double[] simpleData = extractor.extractHistogramData(values,numericField,averageArrayValues);
		divider.setMaxBuckets(values.size() / 2);
		double intervalWidth = divider.getIntervalWidth(simpleData);
		HistogramModel model = processor.buildHistogramModelWithData(
				simpleData, intervalWidth);
		initializeHistogamGrid(model, width, height);
	}




	@Override
	public void setFocusIndex(int focusIndex) {
		// TODO Handle focus
		
	}


}
