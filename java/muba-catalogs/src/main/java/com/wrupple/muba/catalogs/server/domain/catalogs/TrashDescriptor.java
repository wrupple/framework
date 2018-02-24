package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContentNode;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.event.domain.impl.PrimaryKeyField;

public class TrashDescriptor extends CatalogEntryDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1107345618118170025L;


	@Inject
	public TrashDescriptor(@Named(Trash.CATALOG) Class clazz, PrimaryKeyField id,
			NameField name, AnonymouslyVisibleField publicField) {
		super(Trash.CATALOG, serialVersionUID, "Trash", clazz, id, name, publicField);
		putField(new FieldDescriptorImpl().makeKey(HasCatalogId.CATALOG_FIELD, "Catalog", CatalogDescriptor.CATALOG_ID,false));
		putField(new FieldDescriptorImpl().makeKey(HasEntryId.ENTRY_ID_FIELD, "Entry", null,false));
		putField(new FieldDescriptorImpl().makeDefault("restored", "Restore?",  CatalogEntry.BOOLEAN_DATA_TYPE));
		setParent(ContentNode.NUMERIC_ID);
	}

}
