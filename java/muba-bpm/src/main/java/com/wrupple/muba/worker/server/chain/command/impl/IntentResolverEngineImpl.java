package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogQueryRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.event.domain.impl.InvocationImpl;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.worker.domain.IntentResolverContext;
import com.wrupple.muba.worker.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.worker.domain.impl.IntentImpl;
import com.wrupple.muba.worker.domain.impl.WorkerStateImpl;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import org.apache.commons.chain.Context;

import java.util.List;

/**
 * Created by japi on 29/07/17.
 */
public class IntentResolverEngineImpl implements IntentResolverEngine {


    @Override
    public boolean execute(IntentResolverContext context) throws Exception {
        Contract contract = (Contract) context.getRuntimeContext().getServiceContract();

        InvocationImpl resolver = new InvocationImpl( );
        resolver.setDomain(contract.getDomain());
        IntentImpl explicitIntent = new IntentImpl();
        ApplicationStateImpl state = new ApplicationStateImpl();
        FilterData implicitDiscriminator = FilterDataUtils.createSingleFieldFilter(HasCatalogId.CATALOG_FIELD,contract.getCatalog());
        CatalogQueryRequestImpl queryRequest = new CatalogQueryRequestImpl(implicitDiscriminator, Application.CATALOG);
        context.getRuntimeContext().getServiceBus().fireEvent(queryRequest,context.getRuntimeContext(),null);
        Application serviceManifest = (Application) queryRequest.getResults().get(0);

        state.setApplicationValue(serviceManifest);
        List<String> pathTokens =  context.getRuntimeContext().getServiceBus().getIntentInterpret().generatePathTokens(serviceManifest);
        resolver.setSentence(pathTokens);

        explicitIntent.setStateValue(state);
        explicitIntent.setDomain(resolver.getDomain());
        resolver.setEventValue(explicitIntent);
        context.getRuntimeContext().setResult(resolver);

        return CONTINUE_PROCESSING;
    }
}
