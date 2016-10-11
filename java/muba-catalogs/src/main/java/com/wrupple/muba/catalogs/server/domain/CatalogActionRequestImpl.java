package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.bootstrap.domain.FilterData;

public class CatalogActionRequestImpl extends CatalogEntryImpl implements CatalogActionRequest {

	private static final long serialVersionUID = 1743825364474840159L;
	private CatalogEntry entryValue;
	private FilterDataImpl filter;
	private  String action,format,entry,catalog,locale;
	
	public CatalogActionRequestImpl() {
		super();
	}
	
	
	
	public CatalogActionRequestImpl(String domain,String catalog,String action,String entry,String format,CatalogEntry catalogEntry,FilterData filterData) {
		super();
		this.entryValue = catalogEntry;
		this.filter = (FilterDataImpl) filterData;
		this.action = action;
		this.format = format;
		this.entry = entry;
		this.catalog = catalog;
		setDomain(Long.parseLong(domain));
		this.locale=LOCALE_FIELD;
	}

	public CatalogActionRequestImpl(String domain,String locale,String catalog,String action,String entry,String format,CatalogEntry catalogEntry,FilterDataImpl filter) {
		super();
		this.entryValue = catalogEntry;
		this.filter = filter;
		this.action = action;
		this.format = format;
		this.entry = entry;
		this.catalog = catalog;
		setDomain(Long.parseLong(domain));
		this.locale=locale;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public CatalogEntry getEntryValue() {
		return entryValue;
	}
	public void setEntryValue(CatalogEntry catalogEntry) {
		this.entryValue = catalogEntry;
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


	@Override
	public String toString() {
		return "CatalogActionRequestImpl [catalogEntry=" + entryValue + ", filter=" + filter + ", action=" + action + ", format=" + format + ", entry="
				+ entry + ", catalog=" + catalog + ", domain=" + getDomain() + "]";
	}



	@Override
	public void setEntry(Object id) {
		setEntry((String)id);
	}



}
