package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.JavaScriptObject;

final class ParsedExpression extends JavaScriptObject {
	protected ParsedExpression() {
	}

	public native CompiledExpression compile(Evaluator math) /*-{
		return this.compile(math);
	}-*/;

	public native String asString()/*-{
		return this.toString();
	}-*/;

}