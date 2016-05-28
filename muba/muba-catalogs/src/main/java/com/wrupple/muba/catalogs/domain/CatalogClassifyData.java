package com.wrupple.muba.catalogs.domain;

public class CatalogClassifyData {
	String catalogId;
	String fieldId;
	boolean oneToMany;
	
	public CatalogClassifyData(String catalogId, String fieldId,
			boolean oneToMany) {
		this.catalogId = catalogId;
		this.fieldId = fieldId;
		this.oneToMany = oneToMany;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public String getFieldId() {
		return fieldId;
	}

	public boolean isOneToMany() {
		return oneToMany;
	}
	
	
}
