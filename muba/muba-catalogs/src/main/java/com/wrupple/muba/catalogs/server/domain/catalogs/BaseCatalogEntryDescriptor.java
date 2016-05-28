package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.server.domain.AnonymouslyVisibleField;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.NameField;
import com.wrupple.vegetate.server.domain.PrimaryKeyField;

@Singleton
public class BaseCatalogEntryDescriptor extends CatalogDescriptorImpl {
	private static final long serialVersionUID = -5946173049824138363L;

	@Inject
	public BaseCatalogEntryDescriptor(PrimaryKeyField id,NameField name,AnonymouslyVisibleField publicField) {
		super(CatalogEntry.class.getSimpleName(),null,serialVersionUID,CatalogEntry.class.getSimpleName(),id,name);
	}
}
