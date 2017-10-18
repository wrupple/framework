package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;

import java.io.PrintWriter;

public interface EntrySynthesizer {


    /**
     * @param source           child entity from which to synthesize the ancestor
     * @param catalog          child catalog
     * @param excludeInherited exclude fields of higher ancestors
     * @param instrospection   reflection instrospection
     * @param context          TODO
     * @return
     * @throws Exception
     */
    CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
                                         boolean excludeInherited, Instrospection instrospection, CatalogActionContext context) throws Exception;

    void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry regreso, Instrospection instrospection,
                                   CatalogDescriptor catalog) throws Exception;

    CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry result, Instrospection instrospection,
                                       CatalogDescriptor catalog, CatalogActionContext context) throws Exception;

    void processChildInheritance(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId,
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

    Object synthethizeFieldValue(String[] split, CatalogActionContext context) throws Exception;

    public Object getAllegedParentId(CatalogEntry result, Instrospection instrospection, FieldAccessStrategy access) throws ReflectiveOperationException;

}