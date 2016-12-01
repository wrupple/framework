package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.vegetate.domain.VegetateServiceManifest;

public final class JsVegetateServiceManifest extends JavaScriptObject implements VegetateServiceManifest {

	protected JsVegetateServiceManifest() {
	}

	@Override
	public native String getServiceName() /*-{
		return this.serviceName;
	}-*/;

	@Override
	public native String getServiceVersion() /*-{
		return this.serviceVersion;
	}-*/;

	@Override
	public native JsCatalogDescriptor getContractDescriptor() /*-{
		return this.contractDescriptor;
	}-*/;

	@Override
	public List<VegetateServiceManifest> getChildServiceManifests() {
		JsArray<JsVegetateServiceManifest> arr = getChildServiceManifestsArray();
		if (arr == null) {
			return null;
		} else {
			return (List) JsArrayList.arrayAsList(arr);
		}
	}

	@Override
	public String[] getUrlPathParameters() {
		return GWTUtils.convertToJavaStringArray(getUrlPathParametersArray());
	}

	@Override
	public String[] getChildServicePaths() {
		return GWTUtils.convertToJavaStringArray(getChildServicePathsArray());
	}

	public native JsArray<JsVegetateServiceManifest> getChildServiceManifestsArray() /*-{
		return this.childServiceManifests;
	}-*/;

	public native JsArrayString getUrlPathParametersArray() /*-{
		return this.urlPathParameters;
	}-*/;

	public native JsArrayString getChildServicePathsArray() /*-{
		return this.childServicePaths;
	}-*/;

	public native void setServiceName(String serviceName) /*-{
		this.serviceName = serviceName;
	}-*/;

}
