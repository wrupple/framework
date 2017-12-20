package com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface TextInput extends SafeHtmlTemplates {
	@Template("<input type=\"text\" value=\"{0}\" />")
	SafeHtml input(String fileId);
	
	@Template("<div style=\"outline:none;\">{0}</div>")
    SafeHtml div(String contents);

    @Template("<div style=\"outline:none;\" tabindex=\"{0}\">{1}</div>")
    SafeHtml divFocusable(int tabIndex, String contents);

    @Template("<div style=\"outline:none;\" tabindex=\"{0}\" accessKey=\"{1}\">{2}</div>")
    SafeHtml divFocusableWithKey(int tabIndex, char accessKey, String contents);
}
