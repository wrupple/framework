package com.wrupple.vegetate.server.domain;

import com.wrupple.vegetate.domain.CatalogEntry;

public class NameField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -9053014855187693204L;

	public NameField() {
		makeDefault(CatalogEntry.NAME_FIELD, CatalogEntry.NAME_FIELD, "text", CatalogEntry.STRING_DATA_TYPE);
	}
}
