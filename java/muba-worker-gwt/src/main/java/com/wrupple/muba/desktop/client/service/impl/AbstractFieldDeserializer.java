package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.util.List;

public abstract class AbstractFieldDeserializer implements JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer {

	@Override
	public List<FilterCriteria> rewriteFilterListIteratorData(List<FilterCriteria> criteria, FieldDescriptionService descriptor) {
		return criteria;
	}

}
