package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;

@Singleton
public class NonOperativeCatalogReaderInterceptor implements CatalogReaderInterceptor {
private final CatalogRequestInterpret requestInterpret;

@Inject
    public NonOperativeCatalogReaderInterceptor(CatalogRequestInterpret requestInterpret) {
        this.requestInterpret = requestInterpret;
    }

    @Override
	public FilterData interceptQuery(FilterData filterData, CatalogActionContext context, CatalogDescriptor catalog)
			throws Exception {
		return filterData;
	}

	@Override
	public void interceptResult(CatalogEntry originalEntry, CatalogActionContext context, CatalogDescriptor catalog) throws Exception {
		//FIXME this should be done as an ephemeral field evaluator,  anything requires this class is a design flaw
		if(CatalogDescriptor.CATALOG_ID.equals(catalog.getDistinguishedName())){
            CatalogActionRequest childContext = new CatalogActionRequestImpl();

            childContext.setName(DataEvent.READ_ACTION);
            childContext.setCatalog(CatalogDescriptor.CATALOG_ID);
            requestInterpret.evaluateGreatAncestor(context, (CatalogDescriptor) originalEntry,childContext);
		}
	}

}
