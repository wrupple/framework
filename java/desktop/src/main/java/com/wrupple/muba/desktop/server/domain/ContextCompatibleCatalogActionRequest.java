package com.wrupple.muba.desktop.server.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.vegetate.server.domain.FilterDataImpl;

public class ContextCompatibleCatalogActionRequest implements CatalogActionRequest {
	private static final long serialVersionUID = -491720328663344796L;
	private ObjectNode catalogEntry;
	private FilterDataImpl filter;
	private String action, format, entry, catalog, locale;
	private Long domain;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public ObjectNode getCatalogEntry() {
		return catalogEntry;
	}

	public void setCatalogEntry(ObjectNode catalogEntry) {
		this.catalogEntry = catalogEntry;
	}

	public FilterDataImpl getFilter() {
		return filter;
	}

	public void setFilter(FilterDataImpl filter) {
		this.filter = filter;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public Long getDomain() {
		return domain;
	}

	public void setDomain(Long domain) {
		this.domain = domain;
	}

}
