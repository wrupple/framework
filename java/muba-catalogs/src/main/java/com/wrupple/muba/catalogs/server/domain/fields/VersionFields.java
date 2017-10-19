package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.Versioned;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class VersionFields extends FieldDescriptorImpl {

	private static final long serialVersionUID = 3044598766227806430L;

	public VersionFields() {
		makeDefault(Versioned.FIELD, "Version",  CatalogEntry.INTEGER_DATA_TYPE);
		setWriteable(false);
		setCreateable(false);
		setDefaultValue("0");
	}

}
