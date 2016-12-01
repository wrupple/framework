package com.wrupple.muba.catalogs.server.service;

import java.io.PrintWriter;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ContextEvaluationService;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface CatalogEvaluationDelegate {

	static final String SOURCE_CONTEXT = "context";
	//necesaary to explicitly point to context? something.context.old
	static final String SOURCE_OLD = ContextEvaluationService.NAME+".old"+CatalogEntry.FOREIGN_KEY;

	public interface Session {
		void resample(CatalogEntry sample);
	}

	Session newSession(CatalogEntry sample);

	Object getPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry entry,
			DistributiedLocalizedEntry localizedObject, Session session) throws ReflectiveOperationException;

	void setPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry entry, Object value,
			Session session) throws ReflectiveOperationException;

	public CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException;

	CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException;

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

	String getDenormalizedFieldValue(CatalogEntry client, String channelField,Session session, CatalogActionContext context) throws Exception;

	Object synthethizeFieldValue(String token, CatalogActionContext context) throws Exception;

	void evalTemplate(String value, PrintWriter out, String locale, CatalogActionContext ccontext);




}
