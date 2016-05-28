package com.wrupple.vegetate.server.services;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

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
	 * @param catalogDescriptor
	 * @return
	 */
	Object decodePrimaryKeyToken(String targetEntryId,
			CatalogDescriptor catalogDescriptor);

	/** 
	 * Used to decode incoming filter criterias
	 * @param values
	 * @return
	 */
	List<Object> decodePrimaryKeyFilters(List<Object> values);
	
	boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog);

	boolean isPrimaryKey(String vanityId);

}
