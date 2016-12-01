package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.JavaScriptObject;

final class ScopedEvaluator extends JavaScriptObject {
	protected ScopedEvaluator() {
	}

	public native void eval(String expr)/*-{
		this.eval(expr);
	}-*/;
	
	public native String evalExpectingString(String expr)/*-{
		return this.eval(expr);
	}-*/;

	public native void assignToTarget(String targetField, JavaScriptObject target, String variable)/*-{
		target[targetField] = this.get(variable);
	}-*/;

	public native void assignFromSource(JavaScriptObject source, String sourceField, String variableName)/*-{
		this.set(variableName,source[sourceField]);
	}-*/;

	public native Object get(String variable)/*-{
		return this.get(variable);
	}-*/;

	public native void set(String variable, int v)/*-{
		rthis.set(variable, v);
	}-*/;

	public native void set(String variable, boolean v)/*-{
		this.set(variable, v);
	}-*/;

	public native void set(String variable, double v)/*-{
		this.set(variable, v);
	}-*/;

	public native void set(String variable, String v)/*-{
		this.set(variable, v);
	}-*/;

	public native void set(String variable, JavaScriptObject v)/*-{
		this.set(variable, v);
	}-*/;

}