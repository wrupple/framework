package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ExplicitIntent;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SentenceField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -9053014855187693204L;

	@Inject
	public SentenceField() {
		makeDefault( ExplicitIntent.HANDLE_FIELD,  ExplicitIntent.HANDLE_FIELD,  CatalogEntry.STRING_DATA_TYPE);
		setMultiple(true);
	}
}
