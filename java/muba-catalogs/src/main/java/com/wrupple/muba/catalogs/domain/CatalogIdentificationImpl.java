package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;

public class CatalogIdentificationImpl implements CatalogIdentification {

	private static final long serialVersionUID = -7040850909734516615L;

	private String id, name, image;
	private Long domain;
	private boolean anonymouslyVisible;

	public CatalogIdentificationImpl() {
		super();
	}

	public CatalogIdentificationImpl(String catalogId, String name, String image) {
		super();
		setId(catalogId);
		setName(name);
		setImage(image);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String getCatalogType() {
		return CatalogDescriptor.CATALOG_ID;
	}

	public Long getDomain() {
		return domain;
	}

	public void setDomain(Long domain) {
		this.domain = domain;
	}

	public boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	public void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}

}
