package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.CatalogManager;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface SystemCatalogPlugin extends CatalogPlugin, CatalogManager, JSRAnnotationsDictionary, CatalogKeyServices {


    FieldAccessStrategy access();

    boolean isJoinableValueField(FieldDescriptor field);

    Object getAllegedParentId(CatalogEntry result, Instrospection instrospection) throws ReflectiveOperationException;
    /**
     * @param context
     * @param catalog
     * @param field
     * @param fieldValue
     * @return id or collection of ids of created objects
     * @throws Exception
     */
    void createRefereces(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
                         Object fieldValue,CatalogEntry parent, Instrospection instrospection) throws Exception;


		/**
		 * @param source
		 *            child entity from which to synthesize the ancestor
		 * @param catalog
		 *            child catalog
		 * @param excludeInherited
		 *            exclude fields of higher ancestors
		 * @param instrospection
		 *            reflection instrospection
		 * @param context
		 *            TODO
		 * @return
		 * @throws Exception
		 */
		public CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
                                                    boolean excludeInherited, Instrospection instrospection, CatalogActionContext context) throws Exception;
		
		public void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry regreso, Instrospection instrospection,
				CatalogDescriptor catalog) throws Exception;

		CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry result, Instrospection instrospection,
				CatalogDescriptor catalog, CatalogActionContext context) throws Exception;

		void addPropertyValues(CatalogEntry source, CatalogEntry target, CatalogDescriptor catalog,
                               boolean excludeInherited, Instrospection instrospection, DistributiedLocalizedEntry localizedObject) throws Exception;

		void processChild(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId,
				CatalogActionContext readContext, CatalogDescriptor catalog, Instrospection instrospection) throws Exception;

		/**
		 * @param catalogDescriptor
		 * @param field
		 * @param e
		 * @param instrospection
		 * @return a catalog entry or a collection of catalog entries
		 */
		Object getPropertyForeignKeyValue(CatalogDescriptor catalogDescriptor, FieldDescriptor field, CatalogEntry e,
                                          Instrospection instrospection) throws ReflectiveOperationException;

		String getDenormalizedFieldValue(CatalogEntry client, String channelField, Instrospection instrospection, CatalogActionContext context) throws Exception;

		String getDenormalizedFieldValue(FieldDescriptor field, Instrospection instrospection, CatalogEntry entry,
                                         CatalogDescriptor typeIfAvailable) throws ReflectiveOperationException;


	void evalTemplate(String value, PrintWriter out, String locale, CatalogActionContext ccontext);


		<T extends CatalogEntry> List<T> getAvailableCatalogs(CatalogActionContext context) throws Exception;

		CatalogResultCache getCache();
		
		public  CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context);


	public Object synthethizeFieldValue(String[] split, CatalogActionContext context) throws Exception ;


}
