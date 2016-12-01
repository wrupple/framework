package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class FieldDescriptorCell extends AbstractCell<FieldDescriptor> {

	public FieldDescriptorCell(){
		super();
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			FieldDescriptor value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<span style='margin-top:5px; margin-bottom:5px;margin-right:auto; margin-left:auto; display:block; text-align:center'>");
		sb.appendHtmlConstant("<strong>");
		sb.appendEscaped(value.getName());
		sb.appendHtmlConstant("</strong> ");
		sb.appendHtmlConstant("</span>");
	}

}
