package com.wrupple.vegetate.server.domain;

import com.wrupple.vegetate.domain.CatalogEntry;

public class AnonymouslyVisibleField extends FieldDescriptorImpl {

	private static final long serialVersionUID = 5041939515738971631L;

	public AnonymouslyVisibleField() {
		setCreateable(true);
		setDataType(CatalogEntry.BOOLEAN_DATA_TYPE);
		setDetailable(true);
		setWriteable(true);
		setEphemeral(false);
		setFilterable(true);
		setKey(false);
		setFieldId(CatalogEntry.PUBLIC);
		setMultiple(false);
		setName("Public");
		setSortable(false);
		setSummary(false);
		setWidget("checkBox");
	}

}
