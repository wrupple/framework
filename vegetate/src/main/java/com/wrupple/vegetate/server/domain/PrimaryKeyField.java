package com.wrupple.vegetate.server.domain;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.vegetate.domain.CatalogEntry;

public class PrimaryKeyField extends FieldDescriptorImpl {
	private static final long serialVersionUID = -3306030117910181235L;
	
	@Inject
	public PrimaryKeyField(@Named("keyFieldCreatable") Boolean keyFieldCreatable) {
		setCreateable(keyFieldCreatable);
		setDataType(CatalogEntry.INTEGER_DATA_TYPE);
		setDetailable(false);
		setWriteable(false);
		setEphemeral(false);
		setFilterable(true);
		setKey(true);
		setFieldId(CatalogEntry.ID_FIELD);
		setMultiple(false);
		setName("Primary Key");
		setSortable(false);
		setSummary(false);
		setWriteable(false);
		setWidget("text");
	}

}
