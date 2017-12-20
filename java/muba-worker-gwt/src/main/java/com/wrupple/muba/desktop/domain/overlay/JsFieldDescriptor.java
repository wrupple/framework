package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.vegetate.domain.Constraint;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.List;

@SuppressWarnings("serial")
public final class JsFieldDescriptor extends JavaScriptObject implements FieldDescriptor {

	protected JsFieldDescriptor() {
		super();
	}

	@Override
	public native boolean isSummary() /*-{
										return this.summary;
										}-*/;

	@Override
	public native String getFieldId() /*-{
										return this.fieldId;
										}-*/;

	@Override
	public native String getName() /*-{
									return this.name;
									}-*/;

	@Override
	public native String getWidget() /*-{
										return this.widget;
										}-*/;

	@Override
	public native boolean isFilterable() /*-{
											return this.filterable;
											}-*/;

	@Override
	public native boolean isCreateable() /*-{
											return this.createable;
											}-*/;

	@Override
	public native boolean isWriteable() /*-{
										return this.writeable;
										}-*/;

	@Override
	public native boolean isDetailable() /*-{
											return this.detailable;
											}-*/;

	@Override
	public native String getDefaultValue() /*-{
											return this.defaultValue;
											}-*/;

	@Override
	public native String getForeignCatalogName() /*-{
													return this.foreignCatalogName;
													}-*/;

	@Override
	public native boolean isKey() /*-{
									return this.key;
									}-*/;

	@Override
	public List<String> getDefaultValueOptions() {
		if (getDefaultValueOptionsAsJsArray() == null) {
			return null;
		}
		return JsArrayList.arrayAsListOfString(getDefaultValueOptionsAsJsArray());
	}

	public native JsArrayString getDefaultValueOptionsAsJsArray()/*-{
																	return this.defaultValueOptions;
																	}-*/;

	@Override
	public native boolean isEphemeral() /*-{
										return this.ephemeral;
										}-*/;

	@Override
	public native int getDataType() /*-{
									return this.dataType;
									}-*/;

	@Override
	public native boolean isSortable() /*-{
										return this.sortable;
										}-*/;

	@Override
	public native boolean isMultiple() /*-{
										return this.multiple;
										}-*/;

	public native void setMultiple(boolean b) /*-{
												this.multiple = b;
												}-*/;

	@Override
	public native String getCommand() /*-{
										return this.command;
										}-*/;


	public native void setFieldId(String id) /*-{
												this.fieldId = id;
												}-*/;

	public native void setName(String name) /*-{
											this.name = name;
											}-*/;

	@Override
	public native boolean isLocalized() /*-{
										return false;
										}-*/;

	@Override
	public List<String> getProperties() {
		JsArrayString propertiesArray = getPropertiesArray();
		if (propertiesArray == null) {
			return null;
		}
		return GWTUtils.asStringList(propertiesArray);
	}

	public native JsArrayString getPropertiesArray() /*-{
														return this.properties;
														}-*/;

	public JavaScriptObject getPropertiesObject() {

		JsArrayString arr = getPropertiesArray();

		JavaScriptObject regreso = GWTUtils.getPropertiesObject(arr);

		return regreso;
	}

	@Override
	public native String getFormula() /*-{
										return this.formula;
										}-*/;

	@Override
	public List<Constraint> getConstraintsValues() {
		JsArray<JsConstraint> arr = getConstraintsValuesArray();
		if (arr == null) {
			return null;
		}
		return (List) JsArrayList.arrayAsList(arr);
	}

	public native JsArray<JsConstraint> getConstraintsValuesArray()/*-{
																		return this.constraintsValues;
																		}-*/;

	@Override
	public native final boolean alwaysRecalculate() /*-{
													if(this.alwaysRecalculate==null){
													return false;
													}
													return this.alwaysRecalculate;
													}-*/;

	@Override
	public native String getDescription()/*-{
											return this.description;
											}-*/;

	@Override
	public native String getHelp() /*-{
									return this.help;
									}-*/;

	@Override
	public native void setWriteable(boolean b) /*-{
		this.writeable=b;
	}-*/;

	@Override
	public native boolean isHardKey() /*-{
		return this.hardKey;
	}-*/;

	@Override
	public void setInherited(boolean asInherited) {
		
	}

	@Override
	public void setOwnerCatalogId(String catalogId) {
		
	}

	@Override
	public native boolean isMasked() /*-{
		if (this.masked === undefined || this.masked == null) {
			return false;
		} else {
			return this.masked;
		}
	}-*/;

}
