package com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface FileDownloadLink extends SafeHtmlTemplates {
    //TODO  replace setRuntimeContext a hand writted implementation that injects CatalogReadingChanel Url generation services
    @Template("<a href='/vegetate/catalog/user/PersistentFileMetadata/read/{0}/file'>{1}</a>")
	SafeHtml defaultCatalogFileOutput(String fileId, String fileName);
}