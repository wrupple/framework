package com.wrupple.muba.event.domain.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class PrimaryKeyField extends FieldDescriptorImpl {
	private static final long serialVersionUID = -3306030117910181235L;
	
	@Inject
	public PrimaryKeyField(@Named("catalog.createablePrimaryKeys") Boolean keyFieldCreatable) {
		setCreateable(keyFieldCreatable);
		setDataType(CatalogEntry.INTEGER_DATA_TYPE);
		setDetailable(false);
		setWriteable(false);
		setGenerated(false);
		setFilterable(true);
		setKey(true);
		setDistinguishedName(CatalogEntry.ID_FIELD);
		setMultiple(false);
		setName("Primary Key");
		setSortable(false);
		setSummary(false);
		setWriteable(false);
	}

}
