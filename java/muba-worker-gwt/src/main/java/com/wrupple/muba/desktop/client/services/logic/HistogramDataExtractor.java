package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.List;

public interface HistogramDataExtractor  {

	double[] extractHistogramData(List<? extends JavaScriptObject> data,String numericField, boolean averageArrayValues);

}
