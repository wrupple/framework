package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;

public class Argument extends CatalogEntryImpl implements CatalogEntry {

	public static final long serialVersionUID = -1761397712598867210L;
	public static final String CATALOG = "Argument";
	public static final String VALUE = "value";
	
	private double value;

	
	public Argument() {
		super();
	}


	public Argument(String name,double value) {
		super();
		setName(name);
		setValue(value);
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}


	@Override
	public String getCatalogType() {
		return getClass().getSimpleName();
	}
	
	

}
