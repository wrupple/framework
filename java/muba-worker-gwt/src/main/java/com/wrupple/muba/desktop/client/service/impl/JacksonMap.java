package com.wrupple.muba.desktop.client.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class JacksonMap implements Map<String, Object> {

	private final ObjectNode root;

	public JacksonMap(ObjectNode root) {
		super();
		this.root = root;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean containsKey(Object arg0) {
		return root.has((String) arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		throw new IllegalArgumentException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		throw new IllegalArgumentException();
	}

	@Override
	public Object get(Object arg0) {
		JsonNode field = root.get((String) arg0);
		if (field == null) {
			return null;
		} else if (field.canConvertToLong()) {
			return field.asLong();
		} else if (field.isBoolean()) {
			return field.asBoolean();
		} else if (field.isArray()) {
			return field;
		} else if (field.isTextual()) {
			return field.textValue();
		} else {
			return new JacksonMap((ObjectNode) field);
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<String> keySet() {
		Iterator<String> a = root.fieldNames();

		Set<String> regreso = new HashSet<String>(root.size());
		while (a.hasNext()) {
			regreso.add(a.next());
		}

		return regreso;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		throw new IllegalArgumentException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> arg0) {
		throw new IllegalArgumentException();

	}

	@Override
	public Object remove(Object arg0) {
		throw new IllegalArgumentException();
	}

	@Override
	public int size() {
		return root.size();
	}

	@Override
	public Collection<Object> values() {
		throw new IllegalArgumentException();
	}

}
