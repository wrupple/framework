package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class NameField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -9053014855187693204L;

	public NameField() {
		makeDefault(CatalogEntry.NAME_FIELD, CatalogEntry.NAME_FIELD,  CatalogEntry.STRING_DATA_TYPE);
	}
}
