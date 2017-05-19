package com.wrupple.muba.desktop.server.service.impl;

import java.util.List;

import com.wrupple.muba.desktop.server.service.impl.JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.FilterCriteria;

public abstract class AbstractFieldDeserializer implements FieldDeserializer {

	@Override
	public List<FilterCriteria> rewriteFilterListIteratorData(List<FilterCriteria> criteria, FieldDescriptionService descriptor) {
		return criteria;
	}

}
