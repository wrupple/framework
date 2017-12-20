package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.bpm.domain.TaskProcessRequest;

public final class JsTaskProcessRequest extends JavaScriptObject implements
		TaskProcessRequest {

	protected JsTaskProcessRequest() {
	}

	@Override
	public native String getTask() /*-{
		return this.task;
	}-*/;

	@Override
	public native String getProcess() /*-{
		return this.process;
	}-*/;

	public native void setProcess(String p) /*-{
		return this.process = p;
	}-*/;

	public native void setTask(String t) /*-{
		return this.task = t;
	}-*/;

	@Override
	public native JsTransactionApplicationContext getActivityContext() /*-{
		return this.activityContext;
	}-*/;

	public native void setActivityContext(JsTransactionApplicationContext ctx)/*-{
		this.activityContext = ctx;
	}-*/;

}
