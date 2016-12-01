package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.vegetate.domain.FilterCriteria;

public final class JsFilterCriteria extends JavaScriptObject implements FilterCriteria {

	protected JsFilterCriteria() {
		super();
	}
	public static final JsFilterCriteria newFilterCriteria() {
		JsFilterCriteria regreso = createObject().cast();
		return regreso;
	}
	@Override
	public native String getOperator() /*-{
		return this.operator;
	}-*/;
	
	public native JsArrayMixed getValuesArray() /*-{
		if(this.values==null){
			this.values=[];
		}
		return this.values;
	}-*/;		
	
	public native JsArrayMixed getValuesArrayOrNull() /*-{
	return this.values;
}-*/;		

	@Override
	public List<Object> getValues() {
		throw new IllegalArgumentException();
	}

	@Override
	public native void setOperator(String operator) /*-{
		this.operator=operator;
	}-*/;


	public native void setValues(JsArrayString values) /*-{
		this.values=values;
	}-*/;

	
	public native void setValues(JsArrayNumber values) /*-{
		this.values=values;
	}-*/;


	public native void setValues(JsArrayMixed values) /*-{
	this.values=values;
}-*/;


	public native void setPathArray(JsArrayString p) /*-{
		this.path=p;
	}-*/;

	public native JsArrayString getPathArray() /*-{
		if(this.path==null){
			this.path=[];
		}
		return this.path;
	}-*/;

	@Override
	public void setValue(Object value) {
		
		JsArrayMixed values = getValuesArray();
		if(values==null){
			values = JsArrayString.createArray().cast();
			
			setValues(values);
		}

		push(values, value);
		
		
	}

	  private final native void push(JsArrayMixed values,Object value) /*-{
	    values[values.length] = value;
	  }-*/;
	  
	@Override
	public Object getValue() {
		throw new IllegalArgumentException();/*
		JsArrayMixed values = getValuesArray();
		if(values==null){
			return null;
		}else{
			return r(values,0);
		}*/
	}

	
	@Override
	public void addValue(String value) {
		JsArrayMixed values = getValuesArray();
		if(values==null){
			values = JsArrayString.createArray().cast();
			
			setValues(values);
		}
		values.push(value);
	}
	
	
	@Override
	public void setValues(List<Object> vs) {
		JsArrayMixed values = JsArrayString.createArray().cast();
		if(vs==null){
			
		}else{
			for(Object s: vs){
				push(values,s);
			}
		}
		setValues(values);
	}



	@Override
	public void removeValue(String valueToRemove) {
		JsArrayMixed values = getValuesArray();
		if(values==null){
			values = JsArrayMixed.createArray().cast();
		}else{
			JsArrayMixed nuevo = JsArrayMixed.createArray().cast();
			String v;
			for(int i = 0 ; i < values.length(); i++){
				v=values.getString(i);
				if(values.getString(i).equals(valueToRemove)){
				}else{
					nuevo.push(v);
				}
			}
			values=nuevo;
		}
		
		setValues(values);
	}

	@Override
	public String getPath(int tokenIndex) {
		return getPathArray().get(tokenIndex);
	}

	@Override
	public void pushToPath(String field) {
		getPathArray().push(field);
	}

	@Override
	public int getPathTokenCount() {
		return getPathArray().length();
	}

	public native boolean hasValues() /*-{
		return this.values!=null && this.values.length>0 && this.path!=null&&this.path.length>0;
	}-*/;
	
}
