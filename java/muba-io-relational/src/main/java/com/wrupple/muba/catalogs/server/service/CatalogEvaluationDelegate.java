package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.util.Map;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface CatalogEvaluationDelegate {

	static final String SOURCE_OLD = "old";
	static final String SOURCE_CONTEXT = "context";
	static final String SOURCE_ENTRY = "entry";

	public interface Session {
		void resample(CatalogEntry sample);
	}

	Session newSession(CatalogEntry sample);

	Object getPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry entry,
			DistributiedLocalizedEntry localizedObject, Session session) throws RuntimeException;

	void setPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry entry, Object value,
			Session session) throws Exception;

	public CatalogEntry synthesize(CatalogDescriptor catalog) throws Exception;

	CatalogEntry synthethize(CatalogDescriptor catalog, CatalogEntry seed, CatalogDescriptor seedCatalog,
			CatalogEntry newSourceEntry, CatalogEntry oldSourceEntry, CatalogActionContext context,
			Map<String, String> properties) throws Exception;

	Object synthethizeFieldValue(String expression, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog,
			CatalogActionContext context, Session session, FieldDescriptor field) throws IOException, Exception;

	<T extends CatalogEntry> T catalogCopy(CatalogDescriptor catalog, T entry) throws Exception;

	boolean isWriteableProperty(String string, CatalogEntry sample, Session session);

	void setPropertyValue(CatalogDescriptor mainCatalog, String reservedField, CatalogEntry e, Object value,
			Session session) throws Exception;

	Object getAllegedParentId(CatalogEntry result, Session session);

	/**
	 * @param source
	 *            child entity from which to synthesize the ancestor
	 * @param catalog
	 *            child catalog
	 * @param excludeInherited
	 *            exclude fields of higher ancestors
	 * @param session
	 *            reflection session
	 * @param context
	 *            TODO
	 * @return
	 * @throws Exception
	 */
	public CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
			boolean excludeInherited, Session session, CatalogActionContext context) throws Exception;

	CatalogEntry readEntry(CatalogDescriptor catalogId, Object parentId, CatalogActionContext readParentEntry)
			throws Exception;
	
	public void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry regreso, Session session,
			CatalogDescriptor catalog) throws Exception;

	CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry result, Session session,
			CatalogDescriptor catalog, CatalogActionContext context) throws Exception;

	void addPropertyValues(CatalogEntry source, CatalogEntry target, CatalogDescriptor catalog,
			boolean excludeInherited, Session session, DistributiedLocalizedEntry localizedObject) throws Exception;

	void processChild(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId,
			CatalogActionContext readContext, CatalogDescriptor catalog, Session session) throws Exception;

	/**
	 * @param catalogDescriptor
	 * @param field
	 * @param e
	 * @param session
	 * @return a catalog entry or a collection of catalog entries
	 */
	Object getPropertyForeignKeyValue(CatalogDescriptor catalogDescriptor, FieldDescriptor field, CatalogEntry e,
			Session session);



}
