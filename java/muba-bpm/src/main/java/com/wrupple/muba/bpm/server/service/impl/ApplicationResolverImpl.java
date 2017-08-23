package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.bpm.server.service.ApplicationResolver;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.FilterCriteriaImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataImpl;

/**
 * Created by japi on 22/08/17.
 */
public class ApplicationResolverImpl implements ApplicationResolver {
    @Override
    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception {

        if(intent.getCatalog()==null){
            throw new IllegalArgumentException("Intents must provide an input event type (catalog)");
        }

        FilterDataImpl filters = new FilterDataImpl();
        FilterCriteriaImpl inputCriteria = new FilterCriteriaImpl();
        inputCriteria.setOperator(FilterData.EQUALS);
        inputCriteria.setValue(intent.getCatalog());
        filters.addFilter(inputCriteria);

        if(intent.getOutputCatalog()!=null){
            FilterCriteriaImpl outputCriteria = new FilterCriteriaImpl();
            outputCriteria.setOperator(FilterData.EQUALS);
            outputCriteria.setValue(intent.getOutputCatalog());
            filters.addFilter(outputCriteria);
        }

        CatalogActionRequestImpl request = new CatalogActionRequestImpl();
        request.setFilter(filters);
        request.setName(CatalogActionRequest.READ_ACTION);
        request.setCatalog(ApplicationItem.CATALOG);

        RuntimeContext thisContext = context.spawnChild();

        thisContext.setServiceContract(request);
        thisContext.process();

        return thisContext.getConvertedResult();
    }
}
