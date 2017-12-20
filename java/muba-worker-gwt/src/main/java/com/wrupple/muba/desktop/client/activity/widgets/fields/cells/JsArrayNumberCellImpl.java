package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class JsArrayNumberCellImpl extends AbstractCell<JsArrayNumber> implements JsArrayNumberCell {

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			JsArrayNumber value, SafeHtmlBuilder sb) {
		if(value!=null){
			double number;
			for(int i = 0 ; i < value.length(); i++){
				number = value.get(i);
				sb.appendHtmlConstant("<div>");
				sb.append(number);
				sb.appendHtmlConstant("</div>");
			}
		}
	}

}
