package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.annotations.CatalogValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class MathProblem extends ContentNodeImpl implements CatalogEntry {
	private static final long serialVersionUID = 279321914166336671L;

	@Min(0)
	@Max(5)
	private Long solution;

	@ForeignKey(foreignCatalog=Argument.CATALOG)
	private List<Long> arguments;

	@CatalogValue(foreignCatalog=Argument.CATALOG)
	private List<Argument> argumentsValues;

	private Long superAncestorHIdentity;


	public Long getSolution() {
		return solution;
	}
	public void setSolution(Long solution) {
		this.solution = solution;
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
