package com.wrupple.muba.desktop.server.service.impl;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class NumericFieldDataAccessObject extends AbstractFieldDeserializer {

	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean multiple = fdescriptor.isMultiple();

		if (multiple) {
			assert value.isArray();
			int size = value.size();
			ArrayList<Double> list = new ArrayList<Double>(size);
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

	private Double doDeserialize(JsonNode value, FieldDescriptor fdescriptor) {
		// TODO more efficient less error prone? return a "Number" maybe?
		Double regreso;
		if (value.isDouble()) {
			regreso = value.asDouble();
		} else {
			regreso = Double.parseDouble(value.asText());
		}
		return regreso;
	}

}
