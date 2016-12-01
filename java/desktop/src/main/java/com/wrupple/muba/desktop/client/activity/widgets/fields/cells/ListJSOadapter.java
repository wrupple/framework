package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONValue;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.JSOAdapterCell.JSOAdapter;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

public class ListJSOadapter<T> implements JSOAdapter<List<T>> {

	JSOAdapter<T> singletonAdapter;
	
	public ListJSOadapter(JSOAdapter<T> singletonAdapter) {
		super();
		this.singletonAdapter = singletonAdapter;
	}

	@Override
	public List<T> fromJSO(Object value) {
		if(value==null){
			return null;
		}
		JavaScriptObject asJso = (JavaScriptObject) value;
		int legth = GWTUtils.arrayLength(asJso);
		com.google.gwt.json.client.JSONArray array = new com.google.gwt.json.client.JSONArray(asJso);
		List<T> regreso = new ArrayList<T>(legth);
		T tmp;
		JSONValue jso;
		for(int i = 0 ; i < legth ; i++){
			jso = array.get(i);
			tmp = singletonAdapter.fromJSO(jso.toString());
			regreso.add(tmp);
		}
		return regreso;
	}

	@Override
	public JavaScriptObject toJSO(List<T> value) {
		if(value==null){
			return null;
		}
		JsArray<JavaScriptObject>  regreso = JsArrayString.createArray().cast();
		JavaScriptObject temp;
		for(T s : value){
			temp = singletonAdapter.toJSO(s);
			regreso.push(temp);
		}
		return regreso;
	}

}
