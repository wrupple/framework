package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

@SuppressWarnings("serial")
public final  class JsProcessDescriptor extends JsCatalogEntry implements ProcessDescriptor{
	public static final String PROCESS_INSTANCE_FIELD = "PROCESS_INSTANCE_FIELD";
	protected JsProcessDescriptor() {
		super();
	}


	@Override
	public List<JsProcessTaskDescriptor> getProcessStepsValues() {
		JsArray<JsProcessTaskDescriptor> array = getProcessStepsInstancesArray();
		return JsArrayList.arrayAsList(array);
	}
	
	public native JsArrayString getProcessStepsArray() /*-{
		return this.processSteps;
	}-*/;
	
	public native void setProcessStepsArray(JsArrayString processSteps)/*-{
	this.processSteps=processSteps;
}-*/;
	
	public native JsArray<JsProcessTaskDescriptor> getProcessStepsInstancesArray() /*-{
		return this.processStepsValues;
	}-*/;


	@Override
	public List<String> getProcessSteps() {
		return GWTUtils.asStringList(getProcessStepsArray());
	}


	
}
