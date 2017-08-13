package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.service.EventBus;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.FilterCriteriaImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataImpl;
import org.apache.commons.beanutils.BeanUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * Service Bus: where to keep the intent dictionary
 * Created by japi on 2/08/17.
 */
@Singleton
public class EventBusImpl implements EventBus {

    private final RootServiceManifest root;
    private final Map<String,ServiceManifest> serviceDictionary;

    @Inject
    public EventBusImpl(RootServiceManifest root) {
        this.root = root;
        this.serviceDictionary=new HashMap<>();
    }

    //sentence(Explicit intent) with contract
    //returnConfigured (contract with sentence) this doesnt appear to need to exist in server

    @Override
    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception {

        //TODO attempt to find a matching service manifest first

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

    @Override
    public HandlerRegistration addHandler(final ServiceManifest serviceManifes) {
        serviceDictionary.put((String)serviceManifes.getCatalog(),serviceManifes);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                serviceDictionary.remove((String)serviceManifes.getCatalog());
            }
        };
    }


    @Override
    public List<String> resolveContractSentence(CatalogEntry serviceContract) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //FIXME find services registered for this contract's super types

        ServiceManifest handler = serviceDictionary.get(serviceContract.getCatalogType());
        if(handler==null){
            throw new IllegalArgumentException("No contract handler for : "+serviceContract.getCatalogType());
        }

        ServiceManifest currentNode = handler;
        
        List<String> pathTokens = new ArrayList<String>();

        //TODO this way each individual service version has it's own security path, and only the default version is reachable via contract alone... that MAY not be what we want
        while(currentNode.getParentValue()!=null){
            pathTokens.add(currentNode.getVersionDistinguishedName());
            currentNode = currentNode.getParentValue();
        }

        Collections.reverse(pathTokens);

        //insert grammar defined tokens
        List<String> serviceGrammar = handler.getGrammar();

        String tokenValue;
        for(String grammarToken: serviceGrammar){
            tokenValue = BeanUtils.getSimpleProperty(serviceContract, grammarToken);
            pathTokens.add(tokenValue);
        }

        return pathTokens;
    }



}
