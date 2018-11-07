package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;

import java.util.Collection;

public class ContractDescriptorImpl extends AbstractContractDescriptor {

	private static final long serialVersionUID = -89125026499165232L;
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
	public String getCatalogType() {
		return null ;
	}
}
