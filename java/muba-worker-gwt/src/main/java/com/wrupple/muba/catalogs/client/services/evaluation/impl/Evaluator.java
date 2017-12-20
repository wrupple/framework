package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.JavaScriptObject;

final class Evaluator extends JavaScriptObject {
	protected Evaluator() {
	}

	static native Evaluator createEvaluator() /*-{
		return math;
	}-*/;

	public native ParsedExpression parse(String expr)/*-{
		return this.parse(expr);
	}-*/;

	/**
	 * 
	 * var f = math.eval('f(x,b) = v ? a ^ x : x ^ a ', scope);
	 * 
	 * f(8,true);
	 * 
	 */
	public native void eval(String expr, JavaScriptObject scope, String targetField, JavaScriptObject target)/*-{
		target[targetField] = this.eval(expr, scope);
	}-*/;
	
	public native String evalExpectingString(String expr, JavaScriptObject scope)/*-{
		return this.eval(expr, scope);
	}-*/;

	public native void eval(String expr, JavaScriptObject scope) /*-{
		this.eval(expr, scope);
	}-*/;

	public native ScopedEvaluator scoped()/*-{
		return this.parser();
	}-*/;

	public native void importScope(JavaScriptObject scope)/*-{
		if (scope != null) {
			this["import"](scope);
		}
	}-*/;

	public native void eval(String expr)  /*-{
		this.eval(expr);
	}-*/;

}