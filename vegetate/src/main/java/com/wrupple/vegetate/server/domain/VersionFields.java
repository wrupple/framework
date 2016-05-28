package com.wrupple.vegetate.server.domain;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.Versioned;

public class VersionFields extends FieldDescriptorImpl {

	private static final long serialVersionUID = 3044598766227806430L;

	public VersionFields() {
		makeDefault(Versioned.FIELD, "Version", "text", CatalogEntry.INTEGER_DATA_TYPE);
		setWriteable(false);
		setCreateable(false);
		setDefaultValue("0");
	}

}
