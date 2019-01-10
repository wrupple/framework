package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wrupple.muba.catalogs.domain.CatalogActionBroadcast;
import com.wrupple.muba.catalogs.domain.CatalogActionFiltering;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogTransactionState;
import com.wrupple.muba.catalogs.server.domain.CatalogActionBroadcastImpl;
import com.wrupple.muba.catalogs.server.service.TransactionsDictionary;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.newFilterCriteria;

@Singleton
public final class CatalogTransactionStateImpl implements CatalogTransactionState {


    private final Provider<CatalogActionFiltering> catalogActionCommitProvider;
    private final TransactionsDictionary dictionary;

    @Inject
    public CatalogTransactionStateImpl(TransactionsDictionary dictionary,Provider<CatalogActionFiltering> catalogActionCommitProvider) {
        this.catalogActionCommitProvider = catalogActionCommitProvider;
        this.dictionary=dictionary;
    }


    public void preprocess(CatalogActionContext context) throws Exception {

        CatalogActionFiltering preprocessEvent=catalogActionCommitProvider.get();//Extends catalog action request
        preprocessEvent.setName(context.getRequest().getName());
        preprocessEvent.setStateValue(context);
        preprocessEvent.setRequestValue((CatalogActionRequest) context.getRuntimeContext().getServiceContract());
        preprocessEvent.setDomain((Long) context.getNamespaceContext().getId());
        context.getRuntimeContext().getServiceBus().fireEvent(preprocessEvent,context.getRuntimeContext(),null);

    }

    public void postProcess(CatalogActionContext context) throws Exception {
        CatalogActionBroadcast event=new CatalogActionBroadcastImpl((Long) context.getRequest().getDomain(), context.getCatalogDescriptor().getDistinguishedName(),context.getRequest().getName(), context.getResult());
        event.setStateValue(context);
        if(context.getOldValues()!=null){
            event.setOldValues(context.getOldValues());
        }
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


    @Override
    public final boolean execute(CatalogActionContext context) throws Exception {
        Command handler = dictionary.getCommand(context.getRequest().getName());
        if(handler instanceof Filter){
            //FIXME ignores chain convention
            ((Filter) handler).postprocess(context,null);
        }
        preprocess(context);
        handler.execute(context);
        postProcess(context);
        return CONTINUE_PROCESSING;
    }
}
