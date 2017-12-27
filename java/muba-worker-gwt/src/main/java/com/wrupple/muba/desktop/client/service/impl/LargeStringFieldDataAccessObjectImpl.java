package com.wrupple.muba.desktop.client.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.lang.reflect.Type;
import java.util.List;

public class LargeStringFieldDataAccessObjectImpl implements LargeStringFieldDataAccessObject, JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer {
	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor field) {
		return value.asText();
	}

	@Override
	public List<FilterCriteria> rewriteFilterListIteratorData(List<FilterCriteria> criteria, FieldDescriptionService descriptor) {
		return criteria;
	}

	@Override
	public Type getLargeStringClass() {
		return String.class;
	}

	@Override
	public String getStringValue(Object fieldData) {
		return (String) fieldData;
	}

	@Override
	public Object processRawLongString(String s) {
		return s;
	}

}
