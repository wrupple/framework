package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.catalogs.domain.LocalizedString;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.LocaleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.catalogs.server.domain.fields.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.domain.fields.StringValue;

public class LocalizedStringDescriptor extends CatalogEntryDescriptor {
	private static final long serialVersionUID = -4905547001329770733L;

	@Inject
	public LocalizedStringDescriptor(@Named(LocalizedString.CATALOG) Class clazz,
			PrimaryKeyField id, NameField name, AnonymouslyVisibleField publicField,LocaleField local,StringValue value) {
		super(LocalizedString.CATALOG, serialVersionUID, "i18n", clazz, id, name, publicField);
		putField(value);
		putField(local);
	}

}
