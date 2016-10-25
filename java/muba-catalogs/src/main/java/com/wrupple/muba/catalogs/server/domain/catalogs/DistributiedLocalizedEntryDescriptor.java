package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.bootstrap.domain.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.LocaleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.catalogs.server.domain.fields.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.domain.fields.PropertiesField;

public class DistributiedLocalizedEntryDescriptor extends BaseCatalogEntryDescriptor {
	private static final long serialVersionUID = -8884049376144966265L;
	@Inject
	public DistributiedLocalizedEntryDescriptor(@Named(DistributiedLocalizedEntry.CATALOG) Class clazz,
			PrimaryKeyField id, NameField name, AnonymouslyVisibleField publicField,PropertiesField properties,LocaleField locale) {
		super(DistributiedLocalizedEntry.CATALOG, serialVersionUID, "Localized Entries", clazz, id, name, publicField);
		
		putField(new FieldDescriptorImpl().makeKey(HasCatalogId.CATALOG_FIELD, "Catalog", CatalogDescriptor.CATALOG_ID,false));
		putField(new FieldDescriptorImpl().makeKey(HasEntryId.ENTRY_ID_FIELD, "Entry", null,false));
		putField(locale);
		putField(properties);
		
	}

}