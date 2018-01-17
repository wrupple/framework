package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public class RichTextCell extends ProcessDelegatingEditableField<String> {

	public RichTextCell(EventBus bus,
			ProcessContextServices contextServices,
			JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,
			Provider<Process<String, String>> nestedProcess,
			String nestedProcessLocalizedName) {
		super(bus, contextServices, contextParameters, d, mode, nestedProcess,
				nestedProcessLocalizedName);
	}

	@Override
	protected void renderAsInput(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			FieldData<String> viewData) {
		sb.appendEscaped("...");
	}

	@Override
	protected void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			FieldData<String> viewData) {
		sb.append(SimpleSafeHtmlRenderer.getInstance().render(value));
	}

	@Override
	protected String getCurrentInputValue(Element parent,  boolean isEditing) {
		return parent.getInnerHTML();
	}

	@Override
	protected void onValueWillCommit(Element parent,AbstractEditableField.FieldData<String> viewData) {
	}

	@Override
	protected void onWillEnterEditMode(Element parent,AbstractEditableField.FieldData<String> viewData) {
	}

}
