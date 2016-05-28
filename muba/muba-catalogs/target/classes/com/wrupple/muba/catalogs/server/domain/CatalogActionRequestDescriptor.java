package com.wrupple.muba.catalogs.server.domain;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.FieldDescriptorImpl;

@Singleton
public class CatalogActionRequestDescriptor extends CatalogDescriptorImpl {

	private static final long serialVersionUID = -2430392352605382692L;
	@Inject
	public CatalogActionRequestDescriptor(@Named(CatalogActionRequest.CATALOG+".deserializeClass") Class clazz) {
		super(CatalogActionRequest.CATALOG,clazz,serialVersionUID,"Catalog Action Request");
		FieldDescriptorImpl field = new FieldDescriptorImpl();
		field.makeDefault(CatalogEntry.DOMAIN_TOKEN, "Requested Domain", "text", CatalogEntry.STRING_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
		
		field = new FieldDescriptorImpl();
		field.makeDefault("catalog", "Catalog Id", "text", CatalogEntry.STRING_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
		
		field = new FieldDescriptorImpl();
		field.makeDefault("action", "Action", "text", CatalogEntry.STRING_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
		
		field = new FieldDescriptorImpl();
		field.makeKey("entry", "Entry", null, false);
		fieldsValues.put(field.getFieldId(), field);
		
		field = new FieldDescriptorImpl();
		field.makeDefault("format", "Format", "text", CatalogEntry.STRING_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
		
		field = new FieldDescriptorImpl();
		field.makeKey("catalogEntry", "Catalog Entity Payload", null, false);
		field.setKey(false);
		field.setDataType(CatalogEntry.OBJECT_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
		field = new FieldDescriptorImpl();
		field.makeKey("filter", "Filter Data", null, false);
		field.setKey(false);
		field.setDataType(CatalogEntry.OBJECT_DATA_TYPE);
		fieldsValues.put(field.getFieldId(), field);
	}
}
