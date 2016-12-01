package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.desktop.client.bootstrap.state.ReadDesktopMetadata;
import com.wrupple.muba.desktop.client.services.command.ContextServicesNativeApiBuilder;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsContentNode;

class WruppleJsBridge extends ContextServicesNativeApiBuilder {

	// FIXME get rid of super class effectevibley killing all current
	// connections with native js in favor of evaluation service 
	public WruppleJsBridge(CatalogPlaceInterpret placeInterpret, TransactionalActivityAssembly assembly) {
		super(placeInterpret, assembly);
	}

	// MultipleCatalogFileUpload has more expamples of jsni

	static native void setPutCatalogs(JavaScriptObject globalScope) /*-{
																				globalScope.putCatalogs = $entry(@com.wrupple.muba.desktop.client.services.logic.impl.CatalogDescriptionServiceImpl::putCatalogs(Lcom/google/gwt/core/client/JsArray;));
																				}-*/;

	public native static void setTreeSum(JavaScriptObject globalScope) /*-{
																	globalScope.treeSum = $entry(@com.wrupple.muba.desktop.client.services.impl.WruppleJsBridge::treeSum(Lcom/wrupple/muba/desktop/domain/overlay/JsContentNode;Ljava/lang/String;));
																	}-*/;
	
	
	public static native void setPPath(JavaScriptObject globalScope) /*-{
		globalScope.pPath =  function (object,p){
			if(p==null||object==null){
				return null;
			}
			var path = p.split(".");
			var o = object;
			var token ;
			for(var i = 0; i < path.length; i++){
				token = path[i];
				if(o==null){
					return null;
				}
				o = o[token];
			}
			return o;
		};
	}-*/;

	static public native String registerFromWindow(String factory, JavaScriptObject scope) /*-{
		scope[factory] = $wnd[factory];
	}-*/;

	// dotMultiply(.40,valuesFromPath(_VA,["socio","bar"]))
	static public double treeSum(JsContentNode entry, String field) {
		GWT.log("treesum: "+ new JSONObject(entry).toString());
		double childValue = entry.getValueAsDouble();

		JsArray<JsContentNode> children = entry.getChildrenValues();
		if (children == null || children.length() == 0) {
		} else {
			JsContentNode child;
			for (int i = 0; i < children.length(); i++) {
				child = children.get(i);
				if (field == null) {
					childValue = childValue + treeSum(child, field);
				} else {
					if (GWTUtils.hasAttribute(entry, field)) {
						childValue = performAdd(childValue,child,field);
					}
				}
			}

		}

		return childValue;
	}

	private native static double performAdd(double childValue, JsContentNode child, String field) /*-{
		return childValue+child[field];
	}-*/;

	private native static double getDoubleValueFromPath(JsCatalogKey temp, JsArrayString path) /*-{
		var value = temp;
		var token;
		for (var i = 0; i < path.length; i++) {
			token = path[i];
			value = value[token];
		}
		return value;
	}-*/;


	public static JsArrayString getGlobalContextExpressions() {
		return getGlobals(ReadDesktopMetadata.GLOBAL_CONTEXT);
	}

	private native static JsArrayString getGlobals(String func) /*-{
		var f = $wnd[func];
		return f();
	}-*/;

	

}
