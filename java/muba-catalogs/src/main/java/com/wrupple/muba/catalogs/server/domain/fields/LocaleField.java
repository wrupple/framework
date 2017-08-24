package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasLocale;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;

public class LocaleField extends FieldDescriptorImpl {

	private static final long serialVersionUID = 3934113285588652948L;

	public LocaleField() {
		makeDefault(HasLocale.LOCALE_FIELD, "Locale", "text", CatalogEntry.STRING_DATA_TYPE);
	}

}
