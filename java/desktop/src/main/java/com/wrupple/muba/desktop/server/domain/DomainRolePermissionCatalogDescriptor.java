package com.wrupple.muba.desktop.server.domain;

import javax.inject.Inject;

import com.wrupple.muba.bpm.domain.DomainRolePermission;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.FieldDescriptorImpl;
import com.wrupple.vegetate.server.domain.PrimaryKeyField;

public class DomainRolePermissionCatalogDescriptor extends CatalogDescriptorImpl {

	private static final long serialVersionUID = -4663111525857625254L;

	@Inject
	public DomainRolePermissionCatalogDescriptor(PrimaryKeyField key) {
		super(DomainRolePermission.CATALOG, PersistentCatalogEntity.class, serialVersionUID, "Role Permissions", key);
		key.setDataType(CatalogEntry.STRING_DATA_TYPE);
		FieldDescriptorImpl field;

		field = new FieldDescriptorImpl();
		field.makeDefault("role_name", "Role Name", "text", CatalogEntry.STRING_DATA_TYPE);
		// FIXME validate role to assert role is from transaction's domain,
		// Session Context knows how
		// TODO field.setKey(true);
		// field.setForeignCatalogName(DomainRole.CATALOG_TIMELINE);
		fields.put(field.getFieldId(), field);

		field = new FieldDescriptorImpl();
		// FIXME validate permission so it doesnt affect another domain or give
		// access to another domain
		field.makeDefault("permission", "Permission", "text", CatalogEntry.STRING_DATA_TYPE);
		fields.put(field.getFieldId(), field);

	}

}
