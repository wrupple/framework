package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Schema extends CatalogEntryImpl implements ContractDescriptor {
	private static final long serialVersionUID = 3661941861991014932L;


	private List<String> contextExpressions, properties;
	private String keyField;
	public String descriptiveField;

	@CatalogField(ignore = true)
	@CatalogValue(foreignCatalog = FieldDescriptor.CATALOG_ID)
	protected Map<String, FieldDescriptor> fieldsValues;

	public void setKeyField(String idField) {
		this.keyField = idField;
	}

	@Override
	public final String getKeyField() {
		return keyField;
	}

	@Override
	public final String getDescriptiveField() {
		return descriptiveField;
	}

	

	public final List<String> getContextExpressions() {
		return contextExpressions;
	}

	public final void setContextExpressions(List<String> contextExpressions) {
		this.contextExpressions = contextExpressions;
	}

	public final List<String> getProperties() {
		return properties;
	}

	public final void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public void setFieldsValues(Collection<FieldDescriptor> fieldsValues) {
		if (fieldsValues == null) {
			this.fieldsValues = null;
		} else {
			this.fieldsValues = new LinkedHashMap<String, FieldDescriptor>(fieldsValues.size());
			for (FieldDescriptor f : fieldsValues) {
				this.fieldsValues.put(f.getDistinguishedName(), f);
			}

		}
	}

	public Collection<String> getFieldsIds() {
		return fieldsValues == null ? null : fieldsValues.keySet();
	}

	public FieldDescriptor getFieldDescriptor(String id) {
		if (this.fieldsValues == null) {
			return null;
		}

		return this.fieldsValues.get(id);
	}

	public Collection<FieldDescriptor> getFieldsValues() {
		if (fieldsValues == null) {
			return null;
		}
		return fieldsValues.values();
	}

}
