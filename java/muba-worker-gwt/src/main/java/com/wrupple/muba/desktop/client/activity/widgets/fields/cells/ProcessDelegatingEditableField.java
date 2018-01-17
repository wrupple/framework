package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public abstract class ProcessDelegatingEditableField<T> extends AbstractDelegatingEditableField<T> {
	private String nestedProcessLocalizedName;
	private Provider<? extends Process<T, T>> nestedProcess;

	public ProcessDelegatingEditableField(EventBus bus,
			ProcessContextServices contextServices,
			JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,Provider<? extends Process<T, T>> nestedProcessProvider, String nestedProcessLocalizedName) {
		super(bus, contextServices, contextParameters, d, mode);
		this.nestedProcessLocalizedName=nestedProcessLocalizedName;
		this.nestedProcess=nestedProcessProvider;
	}



	@Override
	protected String getProcessLocalizedName() {
		return nestedProcessLocalizedName;
	}

	@Override
	protected Process<T, T> getDelegateProcess() {
		return nestedProcess.get();
	}

}
