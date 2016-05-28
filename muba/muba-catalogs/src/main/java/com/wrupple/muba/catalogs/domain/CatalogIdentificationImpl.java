package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogDescriptor;

public class CatalogIdentificationImpl implements CatalogIdentification {
	
	private static final long serialVersionUID = -7040850909734516615L;
	private String id, name,image;

	
	public CatalogIdentificationImpl() {
		super();
	}

	public CatalogIdentificationImpl(String catalogId, String name,String image) {
		super();
		this.id = catalogId;
		this.name = name;
		this.image=image;
	}

	public String getId() {
		return id;
	}

	public void setId(String catalogId) {
		this.id = catalogId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String getCatalog() {
		return CatalogDescriptor.CATALOG_ID;
	}


	@Override
	public String getIdAsString() {
		return getId();
	}

	@Override 
	public void setIdAsString(String id) {
		setId(id);
	}

	


}
