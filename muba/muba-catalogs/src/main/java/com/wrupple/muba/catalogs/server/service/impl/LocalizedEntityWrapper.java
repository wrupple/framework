package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;
import com.wrupple.vegetate.server.chain.command.I18nProcessing.DistributiedLocalizedEntry;

public class LocalizedEntityWrapper implements DistributiedLocalizedEntry {
	private static final long serialVersionUID = 4051797443336293564L;

	private final HasAccesablePropertyValues entity;
	
	private final String locale;

	private final Long numericCatalogId;

	private final String localeField;

	public LocalizedEntityWrapper(HasAccesablePropertyValues entity,String locale, Long numericCatalogId) {
		super();
		this.entity = entity;
		this.localeField="_"+locale;
		this.locale=locale;
		this.numericCatalogId=numericCatalogId;
	}

	@Override
	public String getIdAsString() {
		return ((CatalogEntry)entity).getIdAsString();
	}

	@Override
	public void setIdAsString(String id) {

	}

	@Override
	public String getName() {
		return ((CatalogEntry)entity).getName();
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public Long getCatalogId() {
		return numericCatalogId;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public String getLocalizedFieldValue(String fieldId) {
		//looks like field_en
		return (String) entity.getPropertyValue(fieldId+localeField);
	}

	@Override
	public Long getCatalogEntryId() {
		return (Long) entity.getId();
	}

	@Override
	public Long getDomain() {
		return (Long) entity.getDomain();
	}

	@Override
	public void setDomain(Long domain) {
		entity.setDomain(domain);
	}


	@Override
	public boolean isAnonymouslyVisible() {
		return entity.isAnonymouslyVisible();
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {
		entity.setAnonymouslyVisible(p);
	}

	@Override
	public String getCatalog() {
		return entity.getCatalog();
	}

	@Override
	public Long getId() {
		return (Long) entity.getId();
	}

	@Override
	public String getImage() {
		return entity.getImage();
	}

	@Override
	public void setCatalogId(String catalog) {
	}

	@Override
	public void setCatalogEntryId(String id) {
	}

	@Override
	public List<String> getProperties() {
		return entity.getProperties();
	}


}
