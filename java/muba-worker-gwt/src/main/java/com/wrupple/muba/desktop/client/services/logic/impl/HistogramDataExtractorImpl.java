package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.wrupple.muba.desktop.client.services.logic.HistogramDataExtractor;

import java.util.List;

public class HistogramDataExtractorImpl implements HistogramDataExtractor {

	@Override
	public double[] extractHistogramData(List<? extends JavaScriptObject> data,
			String numericField, boolean averageArrayValues) {
		double[] regreso = new double[data.size()];
		JavaScriptObject o;
		double sum;
		int arrayLenght;
		for(int i = 0 ; i < data.size(); i++){
			o = data.get(i);
			if(averageArrayValues){
				JsArrayNumber value = getArrayValue(o,numericField);
				sum=0;
				arrayLenght=value.length();
				if(value!=null){
					for(int j = 0 ; j<arrayLenght; j++){
						sum+=value.get(j);
					}
					regreso[i]=sum/arrayLenght;
				}
			}else{
				regreso[i]=getDoubleValue(o, numericField);
			}
		}
		
		return regreso;
	}

	private native double getDoubleValue(JavaScriptObject o, String numericField) /*-{
		var value = o[numericField];
		if(value==null){
			return 0;
		}else{
			return value;
		}
	}-*/;

	private final native JsArrayNumber getArrayValue(JavaScriptObject o, String numericField) /*-{
		return o[numericField];
	}-*/;

}
