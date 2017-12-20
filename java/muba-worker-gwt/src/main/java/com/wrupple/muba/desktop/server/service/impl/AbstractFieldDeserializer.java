package com.wrupple.muba.desktop.server.service.impl;

import com.wrupple.muba.desktop.server.service.impl.JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.util.List;

public abstract class AbstractFieldDeserializer implements FieldDeserializer {

	@Override
	public List<FilterCriteria> rewriteFilterListIteratorData(List<FilterCriteria> criteria, FieldDescriptionService descriptor) {
		return criteria;
	}

}
