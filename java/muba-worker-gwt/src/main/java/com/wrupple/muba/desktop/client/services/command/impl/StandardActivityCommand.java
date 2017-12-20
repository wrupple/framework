package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsNotification;
import com.wrupple.vegetate.domain.CatalogDescriptor;

public class StandardActivityCommand {

	
	/**
	 * determines catalog, entry Id, and actitivity of the next place to be processes 
	 * 
	 * @param targetActivity the next activity to directo the user to
	 * @param select first option to determine place arguments
	 * @param current the current place of the desktop
	 * @param isCatalogEntry 
	 * @return
	 */
	public static DesktopPlace determineExplicitPlaceIntentArguments(String[] targetActivity,JsNotification select, DesktopPlace current,boolean isCatalogEntry){
		String catalog;
		String entryId;
		if(select==null){
			catalog = current==null? null: current.getProperty(CatalogActionRequest.CATALOG_ID_PARAMETER);
			entryId=current==null? null : current.getProperty(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
			
		}else{
			
			if(isCatalogEntry){
				catalog=select.getCatalog();
				entryId=select.getId();
			}else{
				catalog=select.getCatalogId();
				entryId = select.getCatalogEntryId();
			}
		}
		
		
		DesktopPlace result = new DesktopPlace(targetActivity);
		result.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, catalog);
		result.setProperty(CatalogActionRequest.CATALOG_ENTRY_PARAMETER, entryId);
		return result;
	}
	
	public static JsNotification getUserOutputEntry(JavaScriptObject userOutput){
		JsNotification select;
		JsArray<JsNotification> outputArray;
		if(GWTUtils.isArray(userOutput)){
			outputArray = userOutput.cast();
			if(outputArray.length()==0){
				Window.alert("Select something first!");
				throw new IllegalArgumentException();
			}else{
				select = outputArray.get(0);
			}
		}else{
			select = userOutput.cast();
		}
		return select;
	}

	public static void determineFieldUrlParameters(CatalogDescriptor result, DesktopPlace p, JavaScriptObject properties, JsNotification output) {
		JsArray<JsFieldDescriptor> fields = ((JsCatalogDescriptor) result).getFieldArray();
		if (fields != null) {
			String fieldId;
			String urlParameter;
			JsFieldDescriptor field;
			JavaScriptObject jso;
			for (int i = 0; i < fields.length(); i++) {
				field = fields.get(i);
				fieldId = field.getFieldId();
				urlParameter = GWTUtils.getAttribute(properties, fieldId + ExplicitOutputPlace.URL_PARAMETER_POSTFIX);
				if (urlParameter != null) {
					if (field.isMultiple()) {
						jso = GWTUtils.getAttributeAsJavaScriptObject(output, fieldId);
						if (jso != null) {
							p.setProperty(urlParameter, new JSONObject(jso).toString());
						}
					} else {
						p.setProperty(urlParameter, GWTUtils.getAttribute(output, fieldId));
					}
				}
			}
		}
	}
	
}
