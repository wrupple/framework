package com.wrupple.muba.desktop.server.service.impl;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class BooleanFieldDeserializer extends AbstractFieldDeserializer {

	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean multiple = fdescriptor.isMultiple();
		if (multiple) {
			assert value.isArray();
			int size = value.size();
			ArrayList<Boolean> list = new ArrayList<Boolean>(size);
			JsonNode singleValue;
			for (int i = 0; i < size; i++) {
				singleValue = value.get(i);
				list.add(doDeserialize(singleValue, fdescriptor));
			}
			return list;
		} else {
			return doDeserialize(value, fdescriptor);
		}

	}

	private boolean doDeserialize(JsonNode value, FieldDescriptor fdescriptor) {
		boolean regreso;
		if (value.isBoolean()) {
			regreso = value.asBoolean();
		} else {
			String textValue = value.asText();
			if ("true".equalsIgnoreCase(textValue) || "1".equals(textValue)) {
				regreso = true;
			} else {
				regreso = false;
			}
		}

		return regreso;
	}

}
