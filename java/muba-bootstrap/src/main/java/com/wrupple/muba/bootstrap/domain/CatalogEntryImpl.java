package com.wrupple.muba.bootstrap.domain;

import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
public abstract class CatalogEntryImpl implements CatalogEntry {

	
	private Long id,image;
	private String  name;
	@NotNull
	private Long domain;
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

	public final Long getDomain() {
		return domain;
	}

	public final void setDomain(Long domain) {
		this.domain = domain;
	}

	public final boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}

}
