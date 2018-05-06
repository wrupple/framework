package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Instrospection;

import java.util.List;

/**
 * Created by japi on 5/05/18.
 */
public interface ResultSetService {

    CatalogColumnResultSet createResultSet(List<CatalogEntry> foreignResults, CatalogDescriptor foreignCatalog,
                                           String foreignCatalogId, CatalogActionContext context, Instrospection instrospection) throws Exception ;

}
