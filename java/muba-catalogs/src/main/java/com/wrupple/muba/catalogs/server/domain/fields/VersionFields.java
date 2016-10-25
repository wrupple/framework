package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;

public class VersionFields extends FieldDescriptorImpl {

	private static final long serialVersionUID = 3044598766227806430L;

	public VersionFields() {
		makeDefault(Versioned.FIELD, "Version", "text", CatalogEntry.INTEGER_DATA_TYPE);
		setWriteable(false);
		setCreateable(false);
		setDefaultValue("0");
	}

}