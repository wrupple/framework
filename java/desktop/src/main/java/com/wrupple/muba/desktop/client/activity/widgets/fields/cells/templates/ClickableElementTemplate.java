package com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates;

import com.google.gwt.safehtml.shared.SafeHtml;

public interface ClickableElementTemplate  {
	SafeHtml button(String message);
	
	SafeHtml iconWithLabel(String image, String label, String description);
	
	SafeHtml noiconWithLabel( String label, String description);
}
