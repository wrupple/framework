package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.services.logic.OutcomeNarrower;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

public abstract class JavaScriptFieldMatchingNarrower implements OutcomeNarrower {


	private String field;
	private String value;
	private String name;
	
	public JavaScriptFieldMatchingNarrower(String field, String value,String name) {
		super();
		this.field = field;
		this.value = value;
		this.name = name;
	}
	
	
	protected boolean isMatch(int currentIndex) {
		JavaScriptObject object = getObjectForIndex(currentIndex);
		return GWTUtils.performJavaScriptEquality(object, field, value);
	}

	

	protected abstract JavaScriptObject getObjectForIndex(int currentIndex);


	protected String getField() {
		return field;
	}


	protected void setField(String field) {
		this.field = field;
	}


	protected String getValue() {
		return value;
	}


	protected void setValue(String value) {
		this.value = value;
	}


	public String getFieldName() {
		return this.name;
	}
	
	
	
}
