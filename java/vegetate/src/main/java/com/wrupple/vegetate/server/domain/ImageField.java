package com.wrupple.vegetate.server.domain;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

public class ImageField extends FieldDescriptorImpl {

	private static final long serialVersionUID = 5799018378606691520L;

	public ImageField() {
		setCreateable(true);
		setDataType(CatalogEntry.INTEGER_DATA_TYPE);
		setDetailable(true);
		setWriteable(false);
		setEphemeral(false);
		setFilterable(true);
		setKey(true);
		setForeignCatalogName(PersistentImageMetadata.CATALOG);
		setFieldId(PersistentImageMetadata.IMAGE_FIELD);
		setMultiple(false);
		setName(PersistentImageMetadata.IMAGE_FIELD);
		setSortable(false);
		setSummary(true);
		setWriteable(true);
		setWidget("imageKey");
	}
}
