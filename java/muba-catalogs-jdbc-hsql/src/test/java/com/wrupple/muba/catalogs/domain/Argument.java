package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.annotations.CatalogField;

public class Argument extends CatalogEntryImpl implements CatalogEntry {

	public static final long serialVersionUID = -1761397712598867210L;
	public static final String CATALOG = "ProblemRequest";
	public static final String VALUE = "value";
	
	@CatalogField(filterable=true)
	private Long value;

	
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
		return getClass().getSimpleName();
	}
	
	

}
