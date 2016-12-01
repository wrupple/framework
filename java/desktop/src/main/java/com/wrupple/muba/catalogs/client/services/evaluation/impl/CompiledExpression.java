package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.JavaScriptObject;

final class CompiledExpression extends JavaScriptObject {
	protected CompiledExpression() {
	}

	public native void evalAssign(JavaScriptObject scope, String targetField, JavaScriptObject target, boolean recalculate)/*-{
		target[targetField] = this.eval(scope);
	}-*/;

	public native Object eval(JavaScriptObject scope)/*-{
		if (scope == null) {
			return this.eval();
		} else {
			return this.eval(scope);
		}
	}-*/;

}