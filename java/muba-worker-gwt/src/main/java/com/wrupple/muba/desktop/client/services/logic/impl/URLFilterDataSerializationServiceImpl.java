package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.*;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.desktop.client.services.logic.URLFilterDataSerializationService;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.FilterData;

@Singleton
public class URLFilterDataSerializationServiceImpl implements
		URLFilterDataSerializationService {


	@Inject
	public URLFilterDataSerializationServiceImpl() {
		super();
	}

	@Override
	public FilterData deserialize(String filterData) {
		if(filterData==null){
			return null;
		}else{
			try{
				JsArray<JsArrayString> fdata=JsonUtils.safeEval(filterData);
				
				FilterData regreso = parseFilterCriterias(fdata);
				
				return regreso;
			}catch(Exception e){
				GWT.log("maformed filterdata "+filterData,e);
				return null;
			}
		}
	}

	@Override
	public String serialize(FilterData filterData) {
		if(filterData==null){
			return null;
		}else{
			JsFilterData jfilterData = (JsFilterData) filterData;
			jfilterData.clearEmpty();
			
			JsArray<JsArrayString> jso = simplifyCriterias(jfilterData.getFilterArray());
			if(jso==null){
				return null;
			}else{
				return new JSONArray(jso).toString();
			}
		}
	}
	
	private JsArray<JsArrayString> simplifyCriterias(
			JsArray<JsFilterCriteria> filterArray) {
		if(filterArray==null || filterArray.length()==0){
			return null;
		}else{
			 JsArray<JsArrayString> regreso = JavaScriptObject.createArray().cast();
			 JsFilterCriteria criteria;
			 JsArrayString path;
			 String digestedPath ;
			 String operator;
			 JsArrayMixed values;
			 JsArrayString simpleCriteria;
			 String value;
			 for(int i = 0 ; i < filterArray.length(); i++){
				 simpleCriteria = JsArrayString.createArray().cast();
				 criteria = filterArray.get(i);
				 path = criteria.getPathArray();
				 digestedPath = digestPath(path);
				 operator = criteria.getOperator();
				 if(FilterData.EQUALS.equals(operator)){
					 operator = "=";
				 }
				 values = criteria.getValuesArray().cast();
				 simpleCriteria.push(digestedPath);
				 simpleCriteria.push(operator);
				 for( int j = 0 ; j < values.length(); j++){
					 value = values.getString(j);
					 simpleCriteria.push(value);
				 }
				 regreso.push(simpleCriteria);
				 
			 }
			 
			return regreso;
		}
		
	}

	private String digestPath(JsArrayString path) {
		StringBuilder builder = new StringBuilder(path.length()*15);
		String pathtoken;
		for(int i =  0; i < path.length(); i++){
			pathtoken = path.get(i);
			
			builder.append(pathtoken);
			
			if(i<path.length()-1){
				builder.append('.');
			}
			
		}
		
		return builder.toString();
	}

	private FilterData parseFilterCriterias(JsArray<JsArrayString> fdata) {
		JsFilterData regreso = JsFilterData.createObject().cast();
		
		JsArrayString rawCriteria;
		String element;
		JsFilterCriteria criteria;
		for(int i = 0 ; i < fdata.length(); i++){
			rawCriteria = fdata.get(i);
			criteria = JsFilterCriteria.createObject().cast();
			if(rawCriteria.length()<3){
				throw new  IllegalArgumentException("Malformed criteria at index "+i);
			}
			for(int j = 0 ; j < rawCriteria.length(); j++){
				element = rawCriteria.get(j);
				if(j==0){
					JsArrayString fieldArray;
					// path
					if(element.indexOf('.')>0){
						//nested field
						fieldArray = splitFields(element);
					}else{
						//simple field
						fieldArray = JsArrayString.createArray().cast();
						fieldArray.push(element);
					}
					criteria.setPathArray(fieldArray);
				}else if(j==1){
					// operator
					if(element.isEmpty()){
						throw new  IllegalArgumentException("criteria at index "+i+" defines no operator");
					}else{
						if("=".equals(element)){
							criteria.setOperator(FilterData.EQUALS);
						}else if(FilterData.DIFFERENT.equals(element)||FilterData.EQUALS.equals(element)||FilterData.CONTAINS_EITHER.equals(element)||FilterData.LESSEQUALS.equals(element)||FilterData.LESS.equals(element)||FilterData.IN.equals(element)||FilterData.GREATEREQUALS.equals(element)||FilterData.GREATER.equals(element)||FilterData.LIKE.equals(element)||FilterData.REGEX.equals(element)||FilterData.STARTS.equals(element)||FilterData.ENDS.equals(element)){
							criteria.setOperator(element);
						}else{
							throw new  IllegalArgumentException("criteria at index "+i+" defines illegal operator "+element);
						}
					}
				}else if(j>=2){
					criteria.addValue(element);
				}
			}
			regreso.addFilter(criteria);
		}
		
		return regreso;
	}
	
	
	private native JsArrayString splitFields(String element) /*-{
		return element.split(".");
	}-*/;

}
