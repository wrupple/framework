package com.wrupple.muba.desktop.server.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.desktop.server.service.impl.JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

public class LargeStringFieldDataAccessObjectImpl implements LargeStringFieldDataAccessObject, FieldDeserializer {
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
