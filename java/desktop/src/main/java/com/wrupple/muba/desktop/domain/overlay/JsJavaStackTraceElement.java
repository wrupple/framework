package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsJavaStackTraceElement extends JavaScriptObject {

	protected JsJavaStackTraceElement() {
	}

	public native String getDeclaringClass()/*-{
		return this.declaringClass;
	}-*/;

	public native String getMethodName()/*-{
		return this.methodName;
	}-*/;

	public native String getFileName()/*-{
		return this.fileName;
	}-*/;

	public native int getLineNumber()/*-{
		return this.lineNumber;
	}-*/;

}
