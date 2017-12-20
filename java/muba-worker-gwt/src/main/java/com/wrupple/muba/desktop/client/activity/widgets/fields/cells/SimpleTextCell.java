package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.CatalogKeyTemplates;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;

public class SimpleTextCell extends ProcessDelegatingEditableField<String> {

	CatalogKeyTemplates template;

	public SimpleTextCell(EventBus bus, ProcessContextServices contextServices, JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,
			Provider<Process<String, String>> nestedProcessProvider, String nestedProcessLocalizedName) {
		super(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, nestedProcessLocalizedName);

		template = GWT.create(CatalogKeyTemplates.class);
	}

	@Override
	protected void renderAsInput(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
		if (value == null) {
			value = "";
		}
		SafeHtml printable = template.keyInput(value);
		sb.append(printable);
	}

	@Override
	protected void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
		if (value == null) {
			value = "";
		}
		SafeHtml printable = template.keyOutput(value);
		sb.append(printable);
	}

	@Override
	protected String getCurrentInputValue(Element parent, boolean isEditing) {
		if (isEditing) {
			InputElement input = parent.getFirstChildElement().cast();
			return input.getValue();
		} else {
			SpanElement span = parent.getFirstChildElement().cast();
			return span.getInnerText();
		}
	}
}
