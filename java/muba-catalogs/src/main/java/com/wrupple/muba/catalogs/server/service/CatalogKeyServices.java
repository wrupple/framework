package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface CatalogKeyServices {

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
	
	public  String[][] getJoins(SystemCatalogPlugin serverSide,Object clientSide,CatalogDescriptor descriptor,String[][] customJoins, Object domain,String host) throws Exception;
	public  String getIncomingForeignJoinableFieldId(CatalogDescriptor foreignDescriptor,String catalog);
}
