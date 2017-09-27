package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.PersistentImageMetadata;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

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
		setCatalog(PersistentImageMetadata.CATALOG);
		setFieldId(PersistentImageMetadata.IMAGE_FIELD);
		setMultiple(false);
		setName(PersistentImageMetadata.IMAGE_FIELD);
		setSortable(false);
		setSummary(true);
		setWriteable(true);
		setWidget("imageKey");
	}
}
