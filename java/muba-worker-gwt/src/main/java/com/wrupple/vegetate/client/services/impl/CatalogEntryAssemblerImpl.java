package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateColumnResultSet;
import com.wrupple.vegetate.client.services.CatalogEntryAssembler;

public class CatalogEntryAssemblerImpl implements CatalogEntryAssembler {

	public CatalogEntryAssemblerImpl() {
	}
	
	@Override
	public JsArray<JsCatalogEntry> processResultSet(
			JsVegetateColumnResultSet resultSet, String catalogid) {
		JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
		JsArrayString fields = resultSet.getColumnNames();
		if(fields==null){
			return regreso;
		}
		JsCatalogEntry newEntry = null;
		JsArrayMixed fieldContents;
		String fieldId;
		
		
		fieldId = fields.get(0);
		fieldContents = resultSet.getColumn(fieldId);
		int size =fieldContents.length();
		//create empty entries, fill values as fields pass by
		for (int j = 0; j < size; j++) {
			newEntry = JsCatalogEntry.createCatalogEntry(catalogid).cast();
			regreso.push(newEntry);
		}
		
		
		for (int i = 0; i < fields.length(); i++) {
			//field's name
			fieldId = fields.get(i);
			//field contents
			fieldContents = resultSet.getColumn(fieldId);
			if (fieldContents != null) {
				size = fieldContents.length();
				for (int j = 0; j < size; j++) {
					//entry to put field value in
					newEntry = regreso.get(j);
					putFieldValue(newEntry,fieldContents,fieldId,j);
				}
			}
		}
		return regreso;
	}
	
	private native void putFieldValue(JsCatalogKey entry,
			JsArrayMixed fieldContents,String field, int index) /*-{
			entry[field]=fieldContents[index];
	}-*/;

	protected final native void setCatalog(JavaScriptObject entry, String id) /*-{
		entry["catalog"] = id;
	}-*/;
}
