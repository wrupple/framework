/**
 * 
 */
package com.wrupple.muba.catalogs.server.domain.catalogs;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.FieldConstraint;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.fields.NameField;
import com.wrupple.muba.catalogs.server.domain.fields.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.domain.fields.PropertiesField;

/**
 * @author japi
 *
 */
public class FieldConstraintContract extends BaseCatalogEntryDescriptor {
	private static final long serialVersionUID = -4466757991853560819L;

	@Inject
	public FieldConstraintContract(@Named(FieldConstraint.CATALOG_ID) Class clazz, PrimaryKeyField id,
			NameField name, AnonymouslyVisibleField publicField,PropertiesField properties) {
		super(FieldConstraint.CATALOG_ID, serialVersionUID, "Constraints", clazz, id, name, publicField);
		FieldDescriptorImpl field;
		field = new FieldDescriptorImpl().makeDefault("constraint", "Constraint", "listPicker",
				CatalogEntry.STRING_DATA_TYPE);
		// desktop does dictionary sync?
		// field.setDefaultValueOptions(dictionary.get().getAvailableAnnotationNames());
		putField(field);
		putField(properties);
	}

}
