package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

@SuppressWarnings("serial")
public final class JsProcessTaskDescriptor extends JsEntity implements ProcessTaskDescriptor {

	public static final String TRANSACTION_FIELD = "transactionType";

	protected JsProcessTaskDescriptor() {
		super();
	}

	@Override
	public native String getTransactionType() /*-{
		return this.transactionType;
	}-*/;

	@Override
	public native String getCatalogId() /*-{
		return this.catalogId;
	}-*/;

	public native JsArrayString getToolbarsArray() /*-{
		return this.toolbars;
	}-*/;

	@Override
	public List<Long> getToolbars() {
		throw new IllegalArgumentException();
	}

	public native void setCatalogId(String catalog) /*-{
		this.catalogId = catalog;
	}-*/;


	public native void setTransactionType(String type) /*-{
		this.transactionType = type;
	}-*/;

	public JavaScriptObject getTaskPropertiesObject() {
		String catalog = getCatalogId();
		JavaScriptObject regreso = getPropertiesObject();
		GWTUtils.setAttribute(regreso, CatalogActionRequest.CATALOG_ID_PARAMETER, catalog);
		return regreso;
	}

	@Override
	public native String getMachineTaskCommandName() /*-{
		return this.machineTaskCommandName;
	}-*/;

	public native void setMachineTaskCommandName(String machineTaskCommandName) /*-{
		this.machineTaskCommandName = machineTaskCommandName;
	}-*/;

	@Override
	public List<Long> getUserActions() {
		throw new IllegalArgumentException();
	}

	public native JsArrayString getUserActionsArray() /*-{
		return this.userActions;
	}-*/;

	@Override
	public List<JsTaskToolbarDescriptor> getToolbarsValues() {
		JsArray<JsTaskToolbarDescriptor> arr = getToolbarsValuesArray();
		if (arr == null) {
			return null;
		} else {
			List<JsTaskToolbarDescriptor> list = JsArrayList.arrayAsList(arr);
			return list;
		}
	}

	public native JsArray<JsTaskToolbarDescriptor> getToolbarsValuesArray() /*-{
		return this.toolbarsValues;
	}-*/;

	public native JsArray<JsWruppleActivityAction> getUserActionValues() /*-{
		return this.userActionsValues;
	}-*/;

	public native void setUserActionsValues(JsArray<JsWruppleActivityAction> preloaded) /*-{
		this.userActionsValues = preloaded;
	}-*/;


	public native void setCurrentPlaceNavigationFlag(boolean value) /*-{
		this.currentPlaceNavigationFlag = value;
	}-*/;

	public native boolean getCurrentPlaceNavigationFlag() /*-{
		if (this.currentPlaceNavigationFlag == null) {
			return false;
		}
		return this.currentPlaceNavigationFlag;
	}-*/;

	public native Object getStateInstance() /*-{
		return this.stateInstance;
	}-*/;

	public native void setStateInstance(Object instance) /*-{
		this.stateInstance = instance;
	}-*/;

	

	public native boolean isKeepOutputFeature() /*-{
		return false;
	}-*/;

	@Override
	public void setDomain(Long domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getDomain() {
		return null;
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {
		
	}

	@Override
	public native String getVanityId() /*-{
		return this.vanityId;
	}-*/;

	@Override
	public List<String> getUrlTokens() {
		JsArrayString arr = getUrlTokensArray();
		return GWTUtils.asStringList(arr);
	}
	
	public native JsArrayString getUrlTokensArray() /*-{
		return this.urlTokens;
	}-*/;


}
