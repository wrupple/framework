package com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface InlineText extends SafeHtmlTemplates {
	@Template("{0}")
	SafeHtml output(String value);
	
	@Template("<span>{0}</span>")
	SafeHtml span(String value);
	
	@Template("<div id='{0}'></div>")
	SafeHtml div(String id);
}