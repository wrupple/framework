package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;

public class PropertiesField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -8200454801569314734L;

	public PropertiesField() {
		makeDefault("properties", "Properties", "multiText",
				CatalogEntry.STRING_DATA_TYPE);
		setMultiple(true);
		setSummary(false);
	}

}
