package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.vegetate.domain.VegetateUserException;

public final class JsJavaExceptionOverlay extends JavaScriptObject  implements VegetateUserException{

	protected JsJavaExceptionOverlay() {
	}

	public native String getDetailMessage()/*-{
		if (this.localizedMessage != null) {
			return this.localizedMessage;
		} else if (this.message != null) {
			return this.message;
		} else if (this.originalMessage != null) {
			return this.originalMessage;
		} else {
			return this.detailMessage;
		}
	}-*/;

	public native JsJavaExceptionOverlay getCause()/*-{
		return this.cause;
	}-*/;

	public native JsArray<JsJavaStackTraceElement> getStackTrace()/*-{
		return this.stackTrace;
	}-*/;

	@Override
	public native int getErrorCode() /*-{
		if(this.errorCode==null){
			return 0;
		}else{
			return this.errorCode;
		}
	}-*/;

}
