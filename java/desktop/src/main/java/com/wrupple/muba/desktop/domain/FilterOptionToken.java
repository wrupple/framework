package com.wrupple.muba.desktop.domain;

public class FilterOptionToken {

	private String name;
	private String value;

	public FilterOptionToken(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public FilterOptionToken(String name) {
		this(name, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
