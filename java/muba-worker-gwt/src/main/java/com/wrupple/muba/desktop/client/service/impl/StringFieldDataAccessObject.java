package com.wrupple.muba.desktop.client.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.ArrayList;

public class StringFieldDataAccessObject extends AbstractFieldDeserializer {

	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean multiple = fdescriptor.isMultiple();
		if (multiple) {
			assert value.isArray();
			int size = value.size();
			ArrayList<String> list = new ArrayList<String>();
			JsonNode singleValue;
			for (int i = 0; i < size; i++) {
				singleValue = value.get(i);
				list.add(singleValue.asText());
			}
			return list;
		} else {
			return value.asText();
		}

	}

}
