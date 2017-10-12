package com.wrupple.muba.catalogs.server.domain;

import javax.validation.constraints.NotNull;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.annotations.AvailableCommand;
import com.wrupple.muba.catalogs.domain.annotations.ValidCatalogActionRequest;

@ValidCatalogActionRequest
public class CatalogActionRequestImpl  implements CatalogActionRequest {

	private static final long serialVersionUID = 1743825364474840159L;
    private Object entry;
	private Object entryValue;

	private FilterDataImpl filter;

	@AvailableCommand(dictionary=CatalogActionRequest.CATALOG_FIELD)
	private String format;

	@NotNull
	private String catalog;
	private String locale;
	
	private Long domain;
	private Long id,image;
	@NotNull
	@AvailableCommand(dictionary=NAME_FIELD)
	private String  name;
	private boolean anonymouslyVisible;
	private boolean followReferences;

	public void setParentValue(CatalogActionRequest parentValue) {
		this.parentValue = parentValue;
	}

	private CatalogActionRequest parentValue;


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

	public final Long getDomain() {
		return domain;
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

	public CatalogActionRequestImpl(Long domain, String catalog, String action, Object entry, String format,
			CatalogEntry catalogEntry, FilterData filterData) {
		super();
		this.entryValue = catalogEntry;
		this.filter = (FilterDataImpl) filterData;
		this.name = action;
		this.format = format;
		this.entry = entry;
		this.catalog = catalog;
		setDomain(domain);
		this.locale = LOCALE_FIELD;
	}

	public CatalogActionRequestImpl(Long domain, String locale, String catalog, String action, Object entry,
			String format, CatalogEntry catalogEntry, FilterDataImpl filter) {
		super();
		this.entryValue = catalogEntry;
		this.filter = filter;
		this.name = action;
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

	public Object getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(Object catalogEntry) {
		this.entryValue = catalogEntry;
	}

	public FilterDataImpl getFilter() {
		return filter;
	}

	@Override
	public boolean getFollowReferences() {
		return isFollowReferences();
	}

	public void setFilter(FilterDataImpl filter) {
		this.filter = filter;
	}


	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Object getEntry() {
		return entry;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	

	@Override
	public String toString() {
		return "CatalogActionRequestImpl [catalogEntry=" + entryValue + ", filter=" + filter + ", name=" + name
				+ ", format=" + format + ", entry=" + entry + ", catalog=" + catalog + ", domain=" + getDomain() + "]";
	}

	@Override
	public void setEntry(Object id) {
		this.entry=id;
	}

	@Override
	public String getCatalogType() {
		return CATALOG;
	}

	@Override
	public void setDomain(Long domain) {
		if(domain==null){
			this.domain=CatalogEntry.PUBLIC_ID;
		}else{
			this.domain = domain;
		}
	}

	public boolean isFollowReferences() {
		return followReferences;
	}

	@Override
	public void setFollowReferences(boolean followReferences) {
		this.followReferences = followReferences;
	}

	@Override
	public void setFilter(FilterData filter) {
		setFilter((FilterDataImpl)filter);
	}


	@Override
	public Void getParent() {
		return null;
	}

	@Override
	public CatalogActionRequest getParentValue() {
		return parentValue;
	}

	@Override
	public CatalogActionRequest getRootAncestor() {
		return CatalogEntryImpl.getRootAncestor(this);
	}
}
