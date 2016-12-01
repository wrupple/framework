package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.catalogs.domain.ApplicationItem;

@SuppressWarnings("serial")
public final class JsApplicationItem extends JsCatalogEntry implements ApplicationItem {

	protected JsApplicationItem() {
		super();
	}

	public native JsArray<JsApplicationItem> getChildItemsValuesArray() /*-{
		return this.childItemsValues;
	}-*/;

	public native JsArrayNumber getUserActionsArray() /*-{
		return this.userActions;
	}-*/;

	@Override
	public native Double getProcess() /*-{
		return this.process;
	}-*/;

	// TODO desktop variable writer doesnt translate ids into strings
	public native String getProcessAsId() /*-{
		return String(this.process);
	}-*/;

	@Override
	public List<Long> getChildItems() {
		throw new IllegalArgumentException();
	}

	public native void setOutputHandler(String command) /*-{
		this.outputHandler = command;
	}-*/;

	public native void setActivity(String activity) /*-{
		this.activity = activity;
	}-*/;

	public native void setChildItemsArray(JsArrayString children) /*-{
		this.childItems = children;
	}-*/;

	public native JsArrayString getChildItemsArray() /*-{
		return this.childItems;
	}-*/;

	public native double getNumericId() /*-{
		return this.id;
	}-*/;

	@Override
	public native boolean isHijackDesktop() /*-{
		if (this.overrideUserDomain === undefined
				|| this.overrideUserDomain == null) {
			return false;
		}
		return this.overrideUserDomain;
	}-*/;

	@Override
	public native String getApplicationDomain() /*-{
		return this.applicationDomain;
	}-*/;

	@Override
	public void appendChild(int index, ApplicationItem item) {
	}

	@Override
	public void appendChildren(List<ApplicationItem> newChildren) {

	}

	@Override
	public void appendChild(ApplicationItem item) {
	}

	@Override
	public List<? extends ApplicationItem> getChildItemsValues() {
		JsArray<JsApplicationItem> arr = getChildItemsValuesArray();
		if (arr == null) {
			return null;
		} else {
			List<JsApplicationItem> list = JsArrayList.arrayAsList(arr);
			return list;
		}
	}

	@Override
	public void setChildItemsValues(List<? extends ApplicationItem> childInstances) {
		throw new IllegalArgumentException();
	}

	public final native String getActivityPresenter() /*-{
		return this.activityPresenter;
	}-*/;

	public final native String getOutputHandler() /*-{
		return this.outputHandler;
	}-*/;

	public final native String getActivity() /*-{
		return this.activity;
	}-*/;

	public final native JsArrayString getElements() /*-{
		return this.properties;
	}-*/;

	public native final String getWelcomeProcess() /*-{
		return this.welcomeProcess;
	}-*/;

	@Override
	public void setId(Long l) {

	}

	public final native String getRequiredRole() /*-{
		return this.requiredRole;
	}-*/;

	@Override
	public final native void setRequiredRole(String role) /*-{
		this.requiredRole = role;
	}-*/;

	@Override
	public void setProperties(List<String> asList) {
		// not allowed client side (in theory)
	}

	@Override
	public void setOverrideUserDomain(boolean b) {
	}

	@Override
	public List<Long> getRequiredScripts() {
		throw new IllegalArgumentException();
	}

	public native JsArrayString getRequiredScriptsArray()/*-{
		return this.requiredScripts;
	}-*/;

	public native JsArrayString getRequiredStyleSheetsArray()/*-{
		return this.requiredStyleSheets;
	}-*/;

	@Override
	public List<Long> getRequiredStyleSheets() {
		throw new IllegalArgumentException();
	}

	public native JsArray<JsCatalogEntry> getRequiredScriptsValues()/*-{
		return this.requiredScriptsValues;
	}-*/;

	public native JsArray<JsCatalogEntry> getRequiredStyleSheetsValues()/*-{
		return this.requiredStyleSheetsValues;
	}-*/;

	public native JsApplicationItem getParentValue()/*-{
		return this.parentValue;
	}-*/;

	public JsArray<JsApplicationItem> getHierarchy() {
		JsArray<JsApplicationItem> arr = getJsHierarchy();
		if (arr == null) {
			arr = JavaScriptObject.createArray().cast();
			
			
			JsApplicationItem item = this;
			arr.push(item);
			while(item.getParentValue()!=null){
				arr.push(item.getParentValue());
				item = item.getParentValue();
			}
			
			setJsHierarchy(arr);
		}

		return arr;
	}

	public native JsArray<JsApplicationItem> getJsHierarchy()/*-{
		return this.hierarchy;
	}-*/;

	public native void setJsHierarchy(JsArray<JsApplicationItem> s)/*-{
		this.hierarchy = s;
	}-*/;

	public native void setParentValue(JsApplicationItem parentValue) /*-{
		this.parentValue=parentValue;
	}-*/;

	@Override
	public native String getDescription() /*-{
		return this.description;
	}-*/;

	public native String getHost() /*-{
		return this.host;
	}-*/;

	public String[] getUri() {
		JsArray<JsApplicationItem> hierarchy = getHierarchy();
		String[] regreso = new String[hierarchy.length()];
		
		for(int i = 0; i < hierarchy.length(); i++){
			regreso[i]=hierarchy.get(0).getActivity();
		}
		
		return regreso;
	}

}