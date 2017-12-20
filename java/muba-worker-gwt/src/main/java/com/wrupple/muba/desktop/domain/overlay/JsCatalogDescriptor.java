package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.vegetate.domain.AncestryConclusions;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public final class JsCatalogDescriptor extends JavaScriptObject implements CatalogDescriptor {

	protected JsCatalogDescriptor() {
		super();
	}

	@Override
	public native String getParent() /*-{
		return this.parent;
	}-*/;

	@Override
	public native String getDescriptiveField() /*-{
		if (this.descriptiveField == null) {
			this.descriptiveField = @com.wrupple.vegetate.domain.CatalogKey::NAME_FIELD;
		}
		return this.descriptiveField;
	}-*/;

	@Override
	public native String getKeyField() /*-{
		if (this.keyField == null) {
			this.keyField = @com.wrupple.vegetate.domain.CatalogKey::ID_FIELD;
		}
		return this.keyField;
	}-*/;

	@Override
	public native String getCatalogId() /*-{
		return this.catalogId;
	}-*/;

	@Override
	public native String getName() /*-{
		return this.name;
	}-*/;

	@Override
	public native String getClazz() /*-{
		return this.clazz;
	}-*/;

	@Override
	public FieldDescriptor getFieldDescriptor(String id) {
		JsArray<JsFieldDescriptor> arr = getFieldArray();
		JsFieldDescriptor temp;
		String tempId;
		for (int i = 0; i < arr.length(); i++) {
			temp = arr.get(i);
			if (temp != null) {
				tempId = temp.getFieldId();
				if (id.equals(tempId)) {
					return temp;
				}
			}
		}
		return null;
	}

	@Override
	public Collection<String> getFieldNames() {
		List<String> list = JsArrayList.arrayAsListOfString(getFieldNamesAsJsArray());
		return list;
	}

	public native JsArrayString getFieldNamesAsJsArray() /*-{
		return this.fieldNames;
	}-*/;

	@Override
	public List<FieldDescriptor> getOwnedFieldsValues() {
		JsArray<JsFieldDescriptor> fieldArray = getFieldArray();

		if (fieldArray == null) {
			return null;
		}
		List<JsFieldDescriptor> list = JsArrayList.arrayAsList(fieldArray);

		return (List) list;
	}

	public native JsArray<JsFieldDescriptor> getFieldArray() /*-{
		return this.ownedFieldsValues;
	}-*/;

	@Override
	public Iterator<FieldDescriptor> fieldIterator() {
		JsArray<JsFieldDescriptor> fieldArray = getFieldArray();
		List<JsFieldDescriptor> list = JsArrayList.arrayAsList(fieldArray);
		Iterator iterator = list.iterator();
		return iterator;
	}

	@Override
	public native boolean isMergeAncestors() /*-{
		return this.mergeAncestors;
	}-*/;

	@Override
	public boolean isLocalized() {
		return false;
	}

	@Override
	public String getLocalizationStrategy() {
		return null;
	}

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

	public native JsArrayString getContextExpressionsArray() /*-{
		return this.contextExpressions;
	}-*/;

	@Override
	public List<String> getContextExpressions() {
		JsArrayString propertiesArray = getContextExpressionsArray();
		if (propertiesArray == null) {
			return null;
		}
		return GWTUtils.asStringList(propertiesArray);
	}

	public native JavaScriptObject getEntryEvaluationScope() /*-{
		return this.entryEvaluationScope;
	}-*/;

	public native void setEntryEvaluationScope(JavaScriptObject scope) /*-{
		this.entryEvaluationScope = scope;
	}-*/;

	@Override
	public native boolean isRevised() /*-{
		return this.revised;
	}-*/;

	@Override
	public List<String> getAppliedSorts() {
		// only applied server side
		throw new IllegalArgumentException("Applied Sorts only applied server side");
	}

	@Override
	public List<String> getAppliedCriteria() {
		// only applied server side
		throw new IllegalArgumentException("Applied criteria only applied server side");
	}

	@Override
	public native String getCachePolicy() /*-{
		return this.cachePolicy;
	}-*/;

	@Override
	public void setDescriptiveField(String nameField) {

	}

	@Override
	public void setKeyField(String idField) {

	}

	@Override
	public int getForeignKeyCount() {
		return 0;
	}

	@Override
	public native boolean isVersioned() /*-{
		return this.versioned;
	}-*/;

	@Override
	public List<CatalogActionTrigger> getTriggersValues() {
		return null;
	}

	@Override
	public native String getHost() /*-{
	return this.server;
}-*/;

	@Override
	public native String getStorage() /*-{
		return this.storage;
	}-*/;

	
	@Override
	public native void setHost(String host) /*-{

		this.server = host;
	}-*/;

	@Override
	public native String getDomain() /*-{
		return this.domain;
	}-*/;

	@Override
	public void putField(FieldDescriptor field) {
		
	}

	@Override
	public AncestryConclusions getAncestryConclusions() {
		return null;
	}

	@Override
	public void setRevised(boolean b) {
		
	}

	@Override
	public void setVersioned(boolean b) {
		
	}
}
