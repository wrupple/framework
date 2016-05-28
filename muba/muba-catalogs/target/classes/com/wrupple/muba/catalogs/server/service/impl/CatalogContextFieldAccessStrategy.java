package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;

import org.apache.bval.util.AccessStrategy;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;

public class CatalogContextFieldAccessStrategy extends AccessStrategy {

	private final Type javaType;
	private final FieldDescriptor field;

	public CatalogContextFieldAccessStrategy(Type javaType, FieldDescriptor field) {
		super();
		this.javaType = javaType;
		this.field = field;
	}

	@Override
	public Object get(Object arg0) {
		CatalogExcecutionContext context = (CatalogExcecutionContext) arg0;
		HasAccesablePropertyValues entry = (HasAccesablePropertyValues) context.getEntryValue();
		return entry.getPropertyValue(field.getFieldId());
	}

	@Override
	public ElementType getElementType() {
		return ElementType.FIELD;
	}

	@Override
	public Type getJavaType() {
		return javaType;
	}

	@Override
	public String getPropertyName() {
		return field.getFieldId();
	}

	public FieldDescriptor getField() {
		return field;
	}

}