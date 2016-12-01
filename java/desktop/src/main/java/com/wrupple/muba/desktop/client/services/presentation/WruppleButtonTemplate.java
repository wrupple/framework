package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.ClickableElementTemplate;

public interface WruppleButtonTemplate extends SafeHtmlTemplates,
		ClickableElementTemplate {
	//TODO replace with a hand written implementation that can use CatalogReadingChannel services
	@Template("<input type='button' value='{0}' />")
	SafeHtml button(String message);
	
	@Template("<div><img  src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/"+ImageTemplate.SMALL+"\" title=\"{2}\" /></div><span>{1}</span>")
	SafeHtml iconWithLabel(String persistentImageId, String label, String description);
	
	@Template("<div><img  src=\"/static/img/no-image.png\" title=\"{1}\" /></div><span>{0}</span>")
	SafeHtml noiconWithLabel( String label, String description);

}
