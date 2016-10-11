package com.wrupple.muba.catalogs.shared.services;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface PrimaryKeyEncodingService {

	/**
	 * encodes outgoing field values
	 * 
	 * @param rawValue
	 * @param field
	 * @param catalog
	 * @return
	 */
	Object encodeClientPrimaryKeyFieldValue(Object rawValue, FieldDescriptor field,
			CatalogDescriptor catalog);

	/**
	 * decodes incoming field keys from a url token
	 * 
	 * @param targetEntryId
	 * @return
	 */
	Object decodePrimaryKeyToken(String targetEntryId);

	/** 
	 * Used to decode incoming filter criterias
	 * @param values
	 * @return
	 */
	List<Object> decodePrimaryKeyFilters(List<Object> values);
	
	boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog);

	boolean isPrimaryKey(String vanityId);

}
