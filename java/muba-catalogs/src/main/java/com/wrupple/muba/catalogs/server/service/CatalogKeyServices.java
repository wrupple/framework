package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface CatalogKeyServices {

	boolean isJoinableValueField(FieldDescriptor field);

	boolean isLocalJoinField(FieldDescriptor currentJoinableField, CatalogDescriptor descriptor) ;

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
	Object decodePrimaryKeyToken(Object targetEntryId);

	/** 
	 * Used to decode incoming filter criterias
	 * @param values
	 * @return
	 */
	List<Object> decodePrimaryKeyFilters(List<Object> values);
	
	boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog);

	boolean isInheritedField(FieldDescriptor field, CatalogDescriptor owner);

	boolean isPrimaryKey(String vanityId);
	
	  List<CatalogRelation> getJoins(CatalogActionContext serverSide, Object clientSide, CatalogDescriptor descriptor, String[][] customJoins, Object domain, String host) throws Exception;

	  String getFieldWithForeignType(CatalogDescriptor foreignDescriptor, String foreignType);

    boolean isFieldOwnedBy(FieldDescriptor fieldDescriptor, CatalogDescriptor catalogDescriptor);
}
