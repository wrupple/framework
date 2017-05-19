package com.wrupple.muba.desktop.server.service.impl;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class IntegerFieldDataAccessObject extends AbstractFieldDeserializer {

	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean multiple = fdescriptor.isMultiple();
		if (multiple) {
			int size = value.size();
			ArrayList<Long> list = new ArrayList<Long>(size);
			JsonNode singleValue;
			assert value.isArray();
			for (int i = 0; i < size; i++) {
				singleValue = value.get(i);
				list.add(doDeserialize(singleValue, fdescriptor));
			}
			return list;
		} else {
			return doDeserialize(value, fdescriptor);
		}
	}

	private Long doDeserialize(JsonNode value, FieldDescriptor fdescriptor) {
		// TODO a more efficient less error prone way to do this?
		if (value.isIntegralNumber()) {
			return value.asLong();
		} else {
			String text = value.asText();
			if (text.isEmpty()) {
				return null;
			} else {
				Long time = Long.parseLong(text);
				return time;
			}
		}
	}

}
