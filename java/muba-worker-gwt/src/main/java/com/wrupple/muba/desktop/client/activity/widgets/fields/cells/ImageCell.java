package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;

public class ImageCell extends AbstractCell<String> {

	ImageTemplate template;
	
	public ImageCell( ImageTemplate template) {
		super();
		this.template = template;
	}



	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		if(value==null || value.isEmpty()){
			sb.append(template.noImageOutput());
		}else{
			sb.append(template.smallImageOutput(value));
		}
	}

}
