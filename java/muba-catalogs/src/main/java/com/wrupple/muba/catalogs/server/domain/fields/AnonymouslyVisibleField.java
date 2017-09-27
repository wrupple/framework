package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

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
