package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.catalogs.server.domain.fields.PrimaryKeyField;

@Singleton
public class CatalogEntryDescriptor extends CatalogDescriptorImpl {
	private static final long serialVersionUID = -5946173049824138363L;

	@Inject
	public CatalogEntryDescriptor(PrimaryKeyField id,NameField name,AnonymouslyVisibleField publicField) {
		super(CatalogEntry.class.getSimpleName(),null,serialVersionUID,CatalogEntry.class.getSimpleName(),null,id,name,publicField);
	}
	
	protected CatalogEntryDescriptor(String pseudoUniqueId,long machineId,String humanId,Class<? extends CatalogEntry> clazz,PrimaryKeyField id,NameField name,AnonymouslyVisibleField publicField) {
		super(pseudoUniqueId,clazz,machineId,humanId,null,id,name,publicField);
	}
}
