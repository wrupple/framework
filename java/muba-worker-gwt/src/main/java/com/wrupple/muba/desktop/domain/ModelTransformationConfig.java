package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JavaScriptObject;

public final class ModelTransformationConfig extends JavaScriptObject {

	protected ModelTransformationConfig() {
		super();
	}

	/**
	 * @return "transaction" or toolbar Id where data will be set
	 */
	public native String getTarget()/*-{
									return this.modelAlterationTarget;
									}-*/;

	public native void setTarget(String t)/*-{
											this.modelAlterationTarget=t;
											}-*/;

	/**
	 * @return "transaction" or toolbar Id where data will be read
	 */
	public native String getSource()/*-{
									return this.modelAlterationSource;
									}-*/;

	public native void setSource(String s) /*-{
											this.modelAlterationSource=s;
											}-*/;

	/**
	 * @return "selectIndex" or "arrayEnclosed"
	 */
	public native String getPostProcess()/*-{
											return this.modelAlterationPostProcess;
											}-*/;

	public native int getPostProcessSelectionIndex()/*-{
													var raw = this.modelAlterationPostProcessSelectionIndex;
													if(raw==null){
													return 0;
													}else{
													return parseInt(raw);
													}
													}-*/;

	public native void setSourceData(JavaScriptObject data) /*-{
															this.modelAlterationSourceData=data;
															}-*/;

	public native JavaScriptObject getSourceData()/*-{
													return this.modelAlterationSourceData;
													}-*/;

	public native boolean isForceFocus()/*-{
										return this.modelAlterationForceFocus==true||this.modelAlterationForceFocus!=null;
										}-*/;
}
