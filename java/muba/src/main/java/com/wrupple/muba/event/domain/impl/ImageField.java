package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.PersistentImageMetadata;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

import static com.wrupple.muba.event.domain.PersistentCatalogEntity.IMAGE_FIELD;

public class ImageField extends FieldDescriptorImpl {

	private static final long serialVersionUID = 5799018378606691520L;

	public ImageField() {
		setCreateable(true);
		setDataType(CatalogEntry.INTEGER_DATA_TYPE);
		setDetailable(true);
		setWriteable(false);
		setGenerated(false);
		setFilterable(true);
		setKey(true);
		setCatalog(PersistentImageMetadata.CATALOG);
		setDistinguishedName(IMAGE_FIELD);
		setMultiple(false);
		setName(IMAGE_FIELD);
		setSortable(false);
		setSummary(true);
		setWriteable(true);
	}
}
