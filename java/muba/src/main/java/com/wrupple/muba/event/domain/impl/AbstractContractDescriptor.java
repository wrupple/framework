package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;

import java.util.List;

public abstract class AbstractContractDescriptor extends CatalogEntryImpl implements ContractDescriptor {
	private static final long serialVersionUID = 3661941861991014932L;


	private List<String> contextExpressions, properties;
	private String keyField;
	public String descriptiveField;

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


}
