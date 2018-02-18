package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SentenceField extends FieldDescriptorImpl {
	final String FIELD = "sentence";

	private static final long serialVersionUID = -9053014855187693204L;

	@Inject
	public SentenceField() {
		makeDefault( FIELD, FIELD,  STRING_DATA_TYPE);
		setMultiple(true);
	}
}
