package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasProperties;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;

public class MathProblem extends ContentNodeImpl implements CatalogEntry, HasProperties {
	private static final long serialVersionUID = 279321914166336671L;
	private List<String> properties,statement;
	@CatalogKey(foreignCatalog=Argument.CATALOG)
	private List<Long> arguments;
	@CatalogValue(foreignCatalog=Argument.CATALOG)
	private List<Argument> argumentsValues;
	private Long superAncestorHIdentity;

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getStatement() {
		return statement;
	}

	public void setStatement(List<String> statement) {
		this.statement = statement;
	}

	public List<Long> getArguments() {
		return arguments;
	}

	public void setArguments(List<Long> arguments) {
		this.arguments = arguments;
	}

	public List<Argument> getArgumentsValues() {
		return argumentsValues;
	}

	public void setArgumentsValues(List<Argument> argumentsValues) {
		this.argumentsValues = argumentsValues;
	}

	@Override
	public String getCatalogType() {
		return getClass().getSimpleName();
	}

	public Long getSuperAncestorHIdentity() {
		return superAncestorHIdentity;
	}

	public void setSuperAncestorHIdentity(Long superAncestorHIdentity) {
		this.superAncestorHIdentity = superAncestorHIdentity;
	}
	
	
	
}
