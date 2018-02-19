package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogContract;
import com.wrupple.muba.catalogs.server.domain.CatalogContractImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils.newFilterCriteria;

public class CatalogTransaction {


    private final Provider<CatalogActionCommit> catalogActionCommitProvider;

    public CatalogTransaction(Provider<CatalogActionCommit> catalogActionCommitProvider) {
        this.catalogActionCommitProvider = catalogActionCommitProvider;
    }


    public void preprocess(CatalogActionContext context, String action) throws Exception {

        CatalogActionCommit preprocessEvent=catalogActionCommitProvider.get();//Extends catalog action request
        preprocessEvent.setName(action);
        preprocessEvent.setStateValue(context);
        preprocessEvent.setRequestValue((CatalogActionRequest) context.getRuntimeContext().getServiceContract());
        preprocessEvent.setDomain((Long) context.getNamespaceContext().getId());
        context.getRuntimeContext().getServiceBus().fireEvent(preprocessEvent,context.getRuntimeContext(),null);

    }

    public void postProcess(CatalogActionContext context,String catalog,String action, CatalogEntry regreso) throws Exception {
        CatalogContract event=new CatalogContractImpl((Long) context.getRequest().getDomain(), catalog,action, regreso);
        event.setStateValue(context);
        if(context.getOldValues()!=null){
            event.setOldValues(context.getOldValues());
        }
        //cache invalidation trigerer.postprocess(context, context.getRuntimeContext().getCaughtException());
        context.getRuntimeContext().getServiceBus().fireEvent(event,context.getRuntimeContext(),null);
        //pubblishEventsimpl -> AppendImplicityliSuscriptedPeersImpl
        List<FilterCriteria> observers;
        if(event.getExplicitlySuscriptedPeers()==null){
            observers=null;
        }else{
            FilterCriteria people = newFilterCriteria();
            people.setOperator(FilterData.EQUALS);
            people.setValues(new ArrayList<>(event.getExplicitlySuscriptedPeers()));
            people.pushToPath(HasStakeHolder.STAKE_HOLDER_FIELD);
            observers = Collections.singletonList(people);
        }

        event.setDomain((Long) context.getNamespaceContext().getId());

        context.getRuntimeContext().getServiceBus().broadcastEvent(event,context.getCatalogDescriptor(),context.getRuntimeContext(),observers);

    }


}
