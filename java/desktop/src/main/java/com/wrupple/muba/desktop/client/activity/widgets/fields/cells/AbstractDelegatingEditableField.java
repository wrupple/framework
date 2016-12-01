package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.vegetate.domain.FieldDescriptor;
public abstract class AbstractDelegatingEditableField<T> extends AbstractEditableField<T> {

	private class Recepient extends DataCallback<T> {

		@Override
		public void execute() {
			lockAllEvents = false;
			if (result != null) {
				finishEditing(result);
			} else {
				finishEditing(null);
			}
		}

	}

	protected final ProcessContextServices contextServices;
	protected final EventBus bus;
	protected final JavaScriptObject contextParameters;

	public AbstractDelegatingEditableField(EventBus bus, ProcessContextServices contextServices, JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode) {
		super(d, mode);
		this.contextParameters = contextParameters;
		this.contextServices = contextServices;
		if (contextServices == null) {
			throw new IllegalArgumentException("null context services");
		}
		this.bus = bus;
	}

	@Override
	protected void onValueWillCommit(Element parent, FieldData<T> viewdata) {

	}

	@Override
	protected void onEditModeEvent(Context context, Element parent, T value, FieldData<T> viewData, NativeEvent event, ValueUpdater<T> valueUpdater) {
		String type = event.getType();
		int keyCode = event.getKeyCode();
		if ("click".equals(type) || ("keyup".equals(type) && keyCode == KeyCodes.KEY_ENTER)) {
			onWillEnterEditMode(parent, viewData);
		}
	}

	@Override
	protected void onWillEnterEditMode(Element parent, FieldData<T> viewdata) {
		try {
			lockAllEvents = true;
			Process<T, T> delegateProcess = getDelegateProcess();
			Recepient recepient = new Recepient();
			T inputValue = getCurrentInputValue(parent, true);
			if(delegateProcess==null){
				recepient.setResult(inputValue);
				Scheduler.get().scheduleDeferred(recepient);
			}else{
				contextServices.getProcessManager().processSwitch(delegateProcess, getProcessLocalizedName(), inputValue, recepient, contextServices);
			}
		} catch (Exception e) {
			GWT.log("something went wrong invoking process", e);
		}
	}

	protected abstract String getProcessLocalizedName();

	protected abstract Process<T, T> getDelegateProcess();

	public EventBus getBus() {
		return bus;
	}

	public FieldDescriptor getFieldDescriptor() {
		return fieldDescriptor;
	}

	public CatalogAction getMode() {
		return mode;
	}

}
