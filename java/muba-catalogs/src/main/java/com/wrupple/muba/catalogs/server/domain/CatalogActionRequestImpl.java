package com.wrupple.muba.catalogs.server.domain;

import javax.validation.constraints.NotNull;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.annotations.AvailableCommand;
import com.wrupple.muba.catalogs.domain.annotations.ValidCatalogActionRequest;

@ValidCatalogActionRequest
public class CatalogActionRequestImpl  implements CatalogActionRequest {

	private static final long serialVersionUID = 1743825364474840159L;

	private CatalogEntry entryValue;

	private FilterDataImpl filter;
	@NotNull
	@AvailableCommand(dictionary=CatalogActionRequest.CATALOG_ACTION_PARAMETER)
	private String action;
	@AvailableCommand(dictionary=CatalogActionRequest.CATALOG_FIELD)
	private String format;
	private String entry;
	@NotNull
	private String catalog;
	private String locale;
	
	private String domain;
	private Long id,image;
	private String  name;
	private boolean anonymouslyVisible;
	

	public final Long getId() {
		return id;
	}

	public final void setId(Long catalogId) {
		this.id = catalogId;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public final Long getImage() {
		return image;
	}

	public final void setImage(Long image) {
		this.image = image;
	}

	public final String getDomain() {
		return domain;
	}

	public final void setDomain(String domain) {
		this.domain = domain;
	}

	public final boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}

	public CatalogActionRequestImpl() {
		super();
	}

	public CatalogActionRequestImpl(Long domain, String catalog, String action, String entry, String format,
			CatalogEntry catalogEntry, FilterData filterData) {
		super();
		this.entryValue = catalogEntry;
		this.filter = (FilterDataImpl) filterData;
		this.action = action;
		this.format = format;
		this.entry = entry;
		this.catalog = catalog;
		setDomain(domain);
		this.locale = LOCALE_FIELD;
	}

	public CatalogActionRequestImpl(Long domain, String locale, String catalog, String action, String entry,
			String format, CatalogEntry catalogEntry, FilterDataImpl filter) {
		super();
		this.entryValue = catalogEntry;
		this.filter = filter;
		this.action = action;
		this.format = format;
		this.entry = entry;
		this.catalog = catalog;
		setDomain(domain);
		this.locale = locale;
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
		return "CatalogActionRequestImpl [catalogEntry=" + entryValue + ", filter=" + filter + ", action=" + action
				+ ", format=" + format + ", entry=" + entry + ", catalog=" + catalog + ", domain=" + getDomain() + "]";
	}

	@Override
	public void setEntry(Object id) {
		setEntry((String) id);
	}

	@Override
	public String getCatalogType() {
		return CATALOG;
	}

	@Override
	public void setDomain(Long domain) {
		if(domain==null){
			this.domain=null;
		}else{
			this.domain = String.valueOf(domain);
		}
	}

}
