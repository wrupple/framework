package com.wrupple.muba.bootstrap.domain;

import java.util.Collection;

public class ContractDescriptorImpl extends AbstractContractDescriptor {

	private static final long serialVersionUID = -89125026499165232L;
	private final Collection<String> fieldsIds;
	private Class<? extends CatalogEntry> clazz;
	private String distinguishedName;

	
	public ContractDescriptorImpl(Collection<String> fields, Class<? extends CatalogEntry> clazz) {
		super();
		this.fieldsIds = fields;
		this.clazz = clazz;
	}

	@Override
	public Class<? extends CatalogEntry> getClazz() {
		return clazz;
	}


	@Override
	public void setClazz(Class<? extends CatalogEntry> clazz) {
		this.clazz=clazz;
	}
	public final String getDistinguishedName() {
		return distinguishedName;
	}

	public final void setDistinguishedName(String catalogId) {
		this.distinguishedName = catalogId;
	}

	@Override
	public Collection<String> getFieldsIds() {
		return fieldsIds;
	}
	@Override
	public String getCatalogType() {
		return null ;
	}
}
