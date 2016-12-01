package com.wrupple.muba.desktop.client.services.logic;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public interface HistogramDataExtractor  {

	double[] extractHistogramData(List<? extends JavaScriptObject> data,String numericField, boolean averageArrayValues);

}
