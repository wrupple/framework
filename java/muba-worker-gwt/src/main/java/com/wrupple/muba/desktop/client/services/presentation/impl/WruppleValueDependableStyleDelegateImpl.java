package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;

import java.util.Arrays;

public class WruppleValueDependableStyleDelegateImpl implements
		ContentStyleDelegate {
	//TODO ... wrap properties in objects, and implement a patter c'mon!
	private String backgroundColor;
	private String[] backGroundColorFieldTokens;
	private String textColor;
	private String[] colorFieldTokens;
	
	@Override
	public void applyValueStyle(Element element, Object v) {
		JavaScriptObject value=(JavaScriptObject) v;
		Style style = element.getStyle();
		if(backgroundColor!=null){
			String backgroundColorRgb;
			if(backGroundColorFieldTokens==null){
				backgroundColorRgb = GWTUtils.getAttribute(value, backgroundColor);
			}else{
				backgroundColorRgb = getFieldValue(backGroundColorFieldTokens, value);
			}
			if(backgroundColorRgb!=null){
				style.setBackgroundColor("#"+backgroundColorRgb);
			}
		}
		if(textColor!=null){
			String ColorRgb;
			if(colorFieldTokens==null){
				ColorRgb =   GWTUtils.getAttribute(value, textColor);
			}else{
				ColorRgb = getFieldValue(colorFieldTokens, value);
			}
			
			if(ColorRgb!=null){
				style.setColor("#"+ColorRgb);
			}
		}
		
		
	}
	
	private String getFieldValue(String[] localFieldTokens, JavaScriptObject entry){
		String fieldid;
		for(int j = 0; j< localFieldTokens.length; j++){
			fieldid = localFieldTokens[j];
			if(j==localFieldTokens.length-1){
				//lastToken
				return GWTUtils.getAttribute(entry, fieldid);
			}else{
				entry = GWTUtils.getAttributeAsJavaScriptObject(entry, fieldid);
				if(entry==null){
					throw new IllegalArgumentException("Illegal nested path :"+Arrays.toString(localFieldTokens));
				}
			}
		}
		return null;
	}

	@Override
	public String getCSSAttributes( Object v) {
		//FIXME safe color values
		JavaScriptObject value=(JavaScriptObject) v;
		if(backgroundColor!=null){
			String backgroundColorRgb = GWTUtils.getAttribute(value, backgroundColor);
			if(backgroundColorRgb!=null){
				return "background-color:#"+backgroundColorRgb+";";
			}
		}
		if(textColor!=null){
			String ColorRgb = GWTUtils.getAttribute(value, textColor);
			if(ColorRgb!=null){
				return "color:#"+ColorRgb;
			}
		}
		return "";
	}

	public void setBackgroundColor(String backGroundColor) {
		this.backgroundColor = backGroundColor;
		if(backGroundColor!=null&&backGroundColor.contains(".")){
			backGroundColorFieldTokens=backGroundColor.split("\TOKEN_SPLITTER");
		}
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
		if(textColor!=null&&textColor.contains(".")){
			colorFieldTokens=textColor.split("\TOKEN_SPLITTER");
		}
	}


}
