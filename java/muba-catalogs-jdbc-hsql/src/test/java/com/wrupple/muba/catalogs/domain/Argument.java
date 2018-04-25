package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.annotations.CatalogField;

public class Argument extends CatalogEntryImpl implements CatalogEntry {

	public static final long serialVersionUID = -1761397712598867210L;
	public static final String CATALOG = "Argument";
	public static final String VALUE = "value";
	
	@CatalogField(filterable=true)
	private Long value;
	@CatalogField(filterable = true)
	@ForeignKey(foreignCatalog=MathProblem.CATALOG)
	private Long problem;
	@CatalogValue(foreignCatalog=MathProblem.CATALOG)
	private MathProblem problemValue;


	public Argument() {
		super();
	}


	public Argument(String name,Long value) {
		super();
		setName(name);
		setValue(value);
	}


	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}


	@Override
	public String getCatalogType() {
		return CATALOG;
	}


    public void setProblemValue(MathProblem problemValue) {
        this.problemValue = problemValue;
    }

	public MathProblem getProblemValue() {
		return problemValue;
	}

	public Long getProblem() {
		return problem;
	}

	public void setProblem(Long problem) {
		this.problem = problem;
	}
}
