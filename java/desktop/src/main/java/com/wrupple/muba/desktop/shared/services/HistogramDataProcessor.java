package com.wrupple.muba.desktop.shared.services;

import com.wrupple.vegetate.domain.HistogramModel;

public interface HistogramDataProcessor {

	HistogramModel buildHistogramModelWithData(double[] data, double intervalWidth);

}
