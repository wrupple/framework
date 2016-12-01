package com.wrupple.muba.bootstrap.domain;

import java.util.List;

public abstract class AbstractContractDescriptor extends CatalogEntryImpl implements ContractDescriptor {
	private static final long serialVersionUID = 3661941861991014932L;


	private List<String> contextExpressions, properties;

	public void setKeyField(String idField) {

	}

	@Override
	public final String getKeyField() {
		return CatalogEntry.ID_FIELD;
	}

	@Override
	public final String getDescriptiveField() {
		return CatalogEntry.NAME_FIELD;
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
