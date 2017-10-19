package com.wrupple.muba.catalogs.server.domain.fields;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class StringValue extends FieldDescriptorImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -725036726651586302L;

	public StringValue() {
		makeDefault("value", "Value", CatalogEntry.STRING_DATA_TYPE);
	}

}
