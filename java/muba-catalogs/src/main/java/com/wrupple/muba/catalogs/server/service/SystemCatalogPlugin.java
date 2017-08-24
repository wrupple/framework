package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.service.CatalogManager;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;

import java.io.PrintWriter;
import java.util.List;

public interface SystemCatalogPlugin extends CatalogPlugin, CatalogManager, JSRAnnotationsDictionary, CatalogKeyServices {


    FieldAccessStrategy access();

    boolean isJoinableValueField(FieldDescriptor field);

    Object getAllegedParentId(CatalogEntry result, Session session) throws ReflectiveOperationException;
    /**
     * @param context
     * @param catalog
     * @param field
     * @param fieldValue
     * @return id or collection of ids of created objects
     * @throws Exception
     */
    void createRefereces(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
                         Object fieldValue,CatalogEntry parent, Session session) throws Exception;


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
                                          Session session) throws ReflectiveOperationException;

		String getDenormalizedFieldValue(CatalogEntry client, String channelField,Session session, CatalogActionContext context) throws Exception;

		String getDenormalizedFieldValue(FieldDescriptor field, Session session, CatalogEntry entry,
                                         CatalogDescriptor typeIfAvailable) throws ReflectiveOperationException;


	void evalTemplate(String value, PrintWriter out, String locale, CatalogActionContext ccontext);


		CatalogActionContext spawn(CatalogActionContext parent);
		
		CatalogActionContext spawn(RuntimeContext system);
		


		<T extends CatalogEntry> List<T> getAvailableCatalogs(CatalogActionContext context) throws Exception;

		CatalogResultCache getCache();
		
		public  CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context);


	public Object synthethizeFieldValue(String[] split, CatalogActionContext context) throws Exception ;


}
