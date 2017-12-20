package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;

public class FilterCriteriaFieldDelegateImpl implements FilterCriteriaFieldDelegate {
	private  final CatalogUserInterfaceMessages messages;
	
	@Inject
	public FilterCriteriaFieldDelegateImpl(CatalogUserInterfaceMessages messages){
		this.messages=messages;
	}
	
	@Override
	public JsArray<JsArrayString> getOperatorOptions(JsFieldDescriptor field) {
		 JsArray<JsArrayString> regreso= JavaScriptObject.createArray().cast();
		 if(field.isKey()){
				addItem("=",FilterData.EQUALS,regreso);
				addItem(FilterData.DIFFERENT,regreso);
			}else if(field.getDefaultValueOptionsAsJsArray()!=null && field.getDefaultValueOptionsAsJsArray().length()>0){
				addItem("=",FilterData.EQUALS,regreso);
				addItem(FilterData.DIFFERENT,regreso);
			}else{
				int dataType = field.getDataType();
				switch (dataType) {
				case CatalogEntry.BOOLEAN_DATA_TYPE:
					addItem("=",FilterData.EQUALS,regreso);
					addItem(FilterData.DIFFERENT,regreso);
					break;
				case CatalogEntry.INTEGER_DATA_TYPE:
				case CatalogEntry.NUMERIC_DATA_TYPE:
				case CatalogEntry.DATE_DATA_TYPE:
					addItem("=",FilterData.EQUALS,regreso);
					addItem(FilterData.DIFFERENT,regreso);
					addItem(FilterData.GREATER,regreso);
					addItem(FilterData.GREATEREQUALS,regreso);
					addItem(FilterData.LESS,regreso);
					addItem(FilterData.LESSEQUALS,regreso);
					break;
				case CatalogEntry.STRING_DATA_TYPE:
					addItem("=",FilterData.EQUALS,regreso);
					addItem(FilterData.DIFFERENT,regreso);
					addItem(FilterData.GREATER,regreso);
					addItem(FilterData.GREATEREQUALS,regreso);
					addItem(FilterData.LESS,regreso);
					addItem(FilterData.LESSEQUALS,regreso);
					addItem(messages.stringComparisonLike(),FilterData.LIKE,regreso);
					addItem(messages.stringComparisonStartsWith(),FilterData.STARTS,regreso);
					addItem(messages.stringComparisonEndsWith(),FilterData.ENDS,regreso);
					addItem(FilterData.REGEX,regreso);
					break;
				}
			}
		return regreso;
	}

	private void addItem(String name, String value, JsArray<JsArrayString> regreso) {
		JsArrayString arr=JsArrayString.createArray().cast();
		arr.push(name);
		arr.push(value);
		regreso.push(arr);
	}

	private void addItem(String value, JsArray<JsArrayString> regreso) {
		addItem(value,value, regreso);
	}

}
