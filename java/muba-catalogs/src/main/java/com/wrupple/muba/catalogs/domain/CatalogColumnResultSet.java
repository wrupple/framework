package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;

import java.util.HashMap;
import java.util.List;

public class CatalogColumnResultSet implements CatalogResultSet {
	private static final long serialVersionUID = -6018656389644785680L;
	HashMap<String, List<Object>> contents;
	String id, cursor;
	private CatalogDescriptor catalogDescriptor;

	public HashMap<String, List<Object>> getContents() {
		return contents;
	}

	public void setContents(HashMap<String, List<Object>> contents) {
		this.contents = contents;
	}

	@Override
	public String getCatalogType() {
		return "ResultSet";
	}

	@Override
	public String getId() {
		return id;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public String getCursor() {
		return cursor;
	}

	public CatalogDescriptor getCatalogDescriptor() {
		return catalogDescriptor;
	}

	public void setCatalogDescriptor(CatalogDescriptor catalogDescriptor) {
		this.catalogDescriptor = catalogDescriptor;
	}

	public void setId(String distinguishedName) {
		this.id = distinguishedName;
	}

}
