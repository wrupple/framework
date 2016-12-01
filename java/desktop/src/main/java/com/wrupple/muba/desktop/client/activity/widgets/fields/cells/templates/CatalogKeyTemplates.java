package com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface CatalogKeyTemplates extends SafeHtmlTemplates{

	@Template("<input type='text' value='{0}' />")
	SafeHtml keyInput(String key);
	
	@Template("<span>{0}</span>")
	SafeHtml keyOutput(String key);
	
	@Template("<input type='button'  text='x' value='{0}'/>")
	SafeHtml keyDelete(String key);
	
	@Template("<span title='{0}'>{1}</span>")
	SafeHtml value(String key,String name);
	
	@Template("<span title='{0}'>{1}</span>")
	SafeHtml valueOutput(String key,String name);
	
}
