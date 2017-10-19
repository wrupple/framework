package com.wrupple.muba.event.domain
;


import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ProblemRequest extends CatalogEntryImpl implements CatalogEntry {

	public static final long serialVersionUID = -1761397712598867210L;
	public static final String CATALOG = "ProblemRequest";
	public static final String VALUE = "value";
	
	@NotNull
	@Pattern(regexp="add|multiply|[0-9]+(\\.[0-9][0-9]?)?")
	private String value,value2;

	
	public ProblemRequest() {
		super();
		setDomain(CatalogEntry.PUBLIC_ID);
	}


	public ProblemRequest(String value,String value2) {
		this();
		setValue(value);
		setValue2(value2);
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public String getCatalogType() {
		return getClass().getSimpleName();
	}


	public String getValue2() {
		return value2;
	}


	public void setValue2(String value2) {
		this.value2 = value2;
	}

	
	

}
