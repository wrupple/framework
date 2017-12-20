package com.wrupple.muba.desktop.server.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrupple.muba.desktop.server.PatchedDateTimeFormat;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;

public class DateFieldDataAccessObject extends AbstractFieldDeserializer {

	// NOT THREAD SAFE!!! TODO check bug from gwt DateTimeFOrmat that does not
	// allow it to run serverside
	PatchedDateTimeFormat dateFormat;

	@Inject
	public DateFieldDataAccessObject(PatchedDateTimeFormat dateFormat) {
		super();
		this.dateFormat = dateFormat;
	}

	@Override
	public Object deserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean multiple = fdescriptor.isMultiple();
		if (multiple) {
			assert value.isArray();
			int size = value.size();
			ArrayList<Date> list = new ArrayList<Date>();
			JsonNode singleValue;
			for (int i = 0; i < size; i++) {
				singleValue = value.get(i);
				list.add(dodeserializeValue(singleValue, fdescriptor));
			}
			return list;
		} else {
			return dodeserializeValue(value, fdescriptor);
		}

	}

	private Date dodeserializeValue(JsonNode value, FieldDescriptor fdescriptor) {
		boolean numeric = value.isIntegralNumber();
		// FIXME field descriptor should specify if date format is passed as
		// long or on text format
		if (numeric) {
			long time = value.asLong();
			return new Date(time);

		} else {
			String text = value.asText();
			try {
				return dateFormat.parse(text);
			} catch (IllegalArgumentException e1) {
				try {
					long time = Long.parseLong(text);
					return new Date(time);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(text + " is not a valid date");
				}

			}

		}
	}

}
