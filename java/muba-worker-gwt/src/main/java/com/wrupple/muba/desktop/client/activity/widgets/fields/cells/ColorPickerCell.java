package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.TextInput;
import com.wrupple.muba.desktop.client.activity.widgets.fields.colorPicker.ColorPicker;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class ColorPickerCell extends
		AbstractDelegatingEditableField<String> {
	TextInput input;

	public ColorPickerCell(EventBus bus,
			ProcessContextServices contextServices,
			JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode) {
		super(bus, contextServices, contextParameters, d, mode);
		input = GWT.create(TextInput.class);
	}

	@Override
	protected void renderAsInput(
			com.google.gwt.cell.client.Cell.Context context, String value,
			SafeHtmlBuilder sb, AbstractEditableField.FieldData<String> viewData) {
		if (value == null) {
			sb.append(input.input(""));
		} else {
			sb.append(input.input(value));
		}
	}

	@Override
	protected void renderReadOnly(
			com.google.gwt.cell.client.Cell.Context context,
			String value,
			SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
		if (value != null) {
			try{
				Integer.parseInt(value, 16);
				// Use the template to create the Cell's html.
				sb.appendHtmlConstant("<span style='background-color:#"+value+"'>"+value+"</span>");
			}catch(Exception e){
				GWT.log("invalid color "+value,e);
			}
			
		}
	}

	@Override
	protected String getCurrentInputValue(Element parent, boolean isEditing) {
		String value = null;
		if (isEditing) {
			value = parent.getFirstChildElement().getAttribute("value");
		} else {
			value = parent.getFirstChildElement().getFirstChildElement()
					.getInnerText();
		}
		return value;
	}

	@Override
	protected String getProcessLocalizedName() {
		return "Color?";
	}

	@Override
	protected SequentialProcess<String, String> getDelegateProcess() {
		ColorPicker picker = new ColorPicker();
		return SequentialProcess
				.wrap(picker, picker, getProcessLocalizedName());
	}

}
