package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

import java.util.Collection;
import java.util.stream.Collectors;

public class ContractDescriptorImpl extends Schema {

	private static final long serialVersionUID = -89125026499165232L;
	private Class<? extends CatalogEntry> clazz;
	private String distinguishedName;


	public ContractDescriptorImpl(Collection<String> fields, Class<? extends CatalogEntry> clazz) {
		super();
		this.clazz = clazz;
		setFieldsValues(fields.stream().
                map(f-> new FieldDescriptorImpl().makeDefault(f,f,CatalogEntry.STRING_DATA_TYPE)).
                collect(Collectors.toList()));
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
