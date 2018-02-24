package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.LocaleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.event.domain.impl.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.domain.fields.PropertiesField;

public class DistributiedLocalizedEntryDescriptor extends CatalogEntryDescriptor {
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
