package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class SentenceField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -9053014855187693204L;

	public SentenceField() {
		makeDefault( ExplicitIntent.HANDLE_FIELD,  ExplicitIntent.HANDLE_FIELD, "multiText", CatalogEntry.STRING_DATA_TYPE);
		setMultiple(true);
	}
}
