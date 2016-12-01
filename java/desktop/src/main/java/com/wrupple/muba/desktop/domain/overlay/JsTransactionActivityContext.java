package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

public final class JsTransactionActivityContext extends JavaScriptObject {

	protected JsTransactionActivityContext() {
		super();
	}

	public native String getTargetEntryId()/*-{
											return this.targetEntryId;
											}-*/;

	public native void setTargetEntryId(String id)/*-{
													this.targetEntryId = id;
													}-*/;

	public native <T extends JsCatalogKey> T getUserOutput() /*-{
																return this.userOutput;
																}-*/;

	public native <T extends JsCatalogKey> JsArray<T> getUserOutputAsCatalogEntryArray() /*-{
																							return this.userOutput;
																							}-*/;

	public boolean isUserOutputArray() {
		return GWTUtils.isArray(getUserOutput());
	}

	public native void setUserOutput(JavaScriptObject o)/*-{
														this.userOutput = o;
														}-*/;

	public native boolean isCanceled() /*-{
										if (this.canceled == null) {
										return false;
										}
										return this.canceled;
										}-*/;

	public native void setCanceled(boolean c)/*-{
												this.canceled = c;
												}-*/;

	public native JsFilterData getFilterData() /*-{
												return this.filter;
												}-*/;

	public native void setFilterData(JsFilterData f) /*-{
														this.filter = f;
														}-*/;

	public native JsProcessTaskDescriptor getTaskDescriptor()/*-{
																return this.taskDescriptor;
																}-*/;

	public native void setTaskDescriptor(JavaScriptObject taskdescriptor)/*-{
																			this.taskDescriptor = taskdescriptor;
																			}-*/;

	public native void setApplicationItem(JsApplicationItem applicationItem)/*-{
																			this.applicationItem = applicationItem;
																			}-*/;

	public native JsApplicationItem getApplicationItem()/*-{
														return this.applicationItem;
														}-*/;

	/**
	 * @param b
	 *            boolean flag determines wether the current user output's
	 *            source is from recovered state, and thus further committing is
	 *            not necessary
	 * @return previous value of the variable
	 */
	public native boolean setRecoveredOutput(boolean b) /*-{
														var prev = (this.recoveredOutput == null || this.recoveredOutput === undefined) ? false : this.recoveredOutput;
														this.recoveredOutput=b;
														return prev;
														}-*/;

	public native int getCurrentTaskIndex() /*-{
											if(this.currentTaskIndex == null || this.currentTaskIndex === undefined){
											this.currentTaskIndex=0;
											}
											return this.currentTaskIndex;
											}-*/;

	public native void setCurrentTaskIndex(int i) /*-{
		this.currentTaskIndex=i;
	}-*/;

}
