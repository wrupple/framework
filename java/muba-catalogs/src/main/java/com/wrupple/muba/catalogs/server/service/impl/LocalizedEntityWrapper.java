package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.DistributiedLocalizedEntry;

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
	public String getName() {
		return ((CatalogEntry)entity).getName();
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public Long getCatalog() {
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
	public void setId(Object id) {
		entity.setPropertyValue(id, CatalogEntry.ID_FIELD);		
	}
	
	@Override
	public Long getEntry() {
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
	public String getCatalogType() {
		return entity.getCatalogType();
	}

	@Override
	public Long getId() {
		return (Long) entity.getId();
	}

	@Override
	public Long getImage() {
		return (Long) entity.getImage();
	}

	@Override
	public void setCatalog(String catalog) {
	}

	@Override
	public void setEntry(Object id) {
	}

	public CatalogEntry getEntryValue() {
		return entity;
	}

	@Override
	public List<String> getProperties() {
		return entity.getProperties();
	}

	@Override
	public void setProperties(List<String> properties) {
		
	}




}
