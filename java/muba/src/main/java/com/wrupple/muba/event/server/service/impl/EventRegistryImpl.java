package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.event.domain.impl.InvocationImpl;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import com.wrupple.muba.event.server.service.EventRegistry;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
public class EventRegistryImpl implements EventRegistry {

    protected static final Logger log = LogManager.getLogger(EventRegistryImpl.class);

    private final String DICTIONARY = ParentServiceManifest.NAME + "-interpret";
    private final ParentServiceManifest root;
    private final Map<String,List<ServiceManifest>> serviceDictionary;

    private final CatalogFactory factory;
    @Inject
    public EventRegistryImpl(ParentServiceManifest root, CatalogFactory factory) {
        this.root = root;
        this.factory = factory;
        this.serviceDictionary=new HashMap<>();
    }

    @Override
    public ParentServiceManifest getRootService() {
        return root;
    }

    @Override
    public void registerService(Registration registration) {
        registerService(registration.getManifest(), registration.getService(), registration.getContractInterpret(), registration.getParent());
    }

    @Override
    public List<ServiceManifest> resolveHandlers(String eventCatalogName) {

        List<ServiceManifest> list = serviceDictionary.get(eventCatalogName);
        log.trace("[FOUND IMPLICIT TYPE HANDLERS] {}->{}",eventCatalogName,list);
        return list;

    }


    private void registerService(ServiceManifest manifest,
                                Command service,
                                RequestInterpret contractInterpret,
                                ParentServiceManifest parent) {
        Catalog dictionary = getDictionaryFactory().getCatalog(ParentServiceManifest.NAME);
        if (dictionary == null) {
            dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
            if (dictionary == null) {
                dictionary = new CatalogBase();
                getDictionaryFactory().addCatalog(DICTIONARY, dictionary);
            }

            dictionary = new CatalogBase();
            getDictionaryFactory().addCatalog(ParentServiceManifest.NAME, dictionary);
        }
        serviceDictionaryput((String)manifest.getCatalog(),manifest);
        dictionary.addCommand(manifest.getServiceId(), service);
        if (parent == null) {
            parent = getRootService();
        }
        parent.register(manifest);
        registerContractInterpret(manifest,contractInterpret);
    }

    private void serviceDictionaryput(String catalog, ServiceManifest manifest) {
        List<ServiceManifest> list = serviceDictionary.get(catalog);
        if(list==null){
            list = new ArrayList<>(2);
            serviceDictionary.put(catalog,list);
        }
        if(list.contains(manifest)){
            throw new IllegalStateException("duplicate registration of service");
        }
        list.add(manifest);
        log.trace("[NEW IMPLICIT TYPE HANDLER] {}->{}",catalog,manifest);
    }

    @Override
    public void registerService(ServiceManifest manifest,
                                Command service,
                                RequestInterpret contractInterpret) {
        registerService(manifest,service,contractInterpret,getRootService());
    }

    @Override
    public void registerService( ServiceManifest manifest,
                                 Command service) {
        registerService(manifest,service,null,getRootService());
    }




    @Override
    public CatalogFactory getDictionaryFactory() {
        return factory;
    }

    private void registerContractInterpret(ServiceManifest manifest,
                                           RequestInterpret service) {
        if(service!=null){
            Catalog dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
            if (dictionary == null) {
                dictionary = getDictionaryFactory().getCatalog(ParentServiceManifest.NAME);
                if (dictionary == null) {
                    dictionary = new CatalogBase();
                    getDictionaryFactory().addCatalog(ParentServiceManifest.NAME, dictionary);
                }
                dictionary = new CatalogBase();
                getDictionaryFactory().addCatalog(DICTIONARY, dictionary);

            }
            dictionary.addCommand(manifest.getServiceId(), service);
        }
    }



    @Override
    public RequestInterpret getExplicitIntentInterpret(RuntimeContext req) {
        return (RequestInterpret) getDictionaryFactory().getCatalog(DICTIONARY).getCommand(req.getServiceManifest().getServiceId());
    }

    @Override
    public List<String> resolveContractSentence(CatalogEntry serviceContract) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //FIXME find services registered for this contract's super types

        ServiceManifest handler = serviceDictionaryget(serviceContract.getCatalogType());
        if(handler==null){
            throw new IllegalArgumentException("No contract handler for : "+serviceContract.getCatalogType());
        }

        List<String> pathTokens = generatePathTokens(handler);
        //insert grammar defined tokens
        List<String> serviceGrammar = handler.getGrammar();
        String tokenValue;
        for(String grammarToken: serviceGrammar){
            tokenValue = BeanUtils.getSimpleProperty(serviceContract, grammarToken);
            pathTokens.add(tokenValue);
        }

        return pathTokens;
    }

    private ServiceManifest serviceDictionaryget(String catalogType) {
        List<ServiceManifest> list = resolveHandlers(catalogType);
        if(list==null){
            return null;
        }else{
            return list.get(0);
        }
    }

    public List<String> generatePathTokens(ServiceManifest handler) {
        ServiceManifest currentNode = handler;

        List<String> pathTokens = new ArrayList<String>();

        //TODO this way each individual service version has it's own security path, and only the default version is reachable via contract alone... that MAY not be what we want, fixme to expose all versions?
        while(currentNode.getParentValue()!=null){
            pathTokens.add(currentNode.getDistinguishedName());
            currentNode = currentNode.getParentValue();
        }

        Collections.reverse(pathTokens);


        return pathTokens;
    }


    @Override
    public Invocation resolveIntent(Contract intent, RuntimeContext context) throws Exception {
        String input = (String) intent.getCatalog();

        if(input!=null) {

            ServiceManifest serviceManifest = serviceDictionaryget(input);

            // check super type support
            if(serviceManifest==null){
                log.debug("No service for intent type {}",input);
                CatalogReadRequestImpl read = new CatalogReadRequestImpl(input,CatalogDescriptor.CATALOG_ID);
                read.setDomain(intent.getDomain());
                CatalogDescriptor intentDescriptor = context.getServiceBus().fireEvent(read,context,null);
                while(serviceManifest==null&&intentDescriptor.getParentValue()!=null){
                    serviceManifest = serviceDictionaryget(intentDescriptor.getParentValue().getDistinguishedName());
                    log.debug("No service for intent type {}",intentDescriptor.getParentValue().getDistinguishedName());

                    intentDescriptor = intentDescriptor.getParentValue();
                }
            }

           return resolveIntent(intent,serviceManifest,context);
        }
        return null;

    }

    @Override
    public Invocation resolveIntent(Contract implicitRequestContract, ServiceManifest serviceManifest, RuntimeContext parentTimeline) {
        log.trace("will resolve implicit handler for {}",implicitRequestContract);
        if (serviceManifest != null) {
            return createIntent(parentTimeline, implicitRequestContract, serviceManifest);
        }
        log.warn("could not find implicit handler for event");
        return null;
    }


    private Invocation createIntent(RuntimeContext context, Contract intent, ServiceManifest serviceManifest) {

        List<String> pathTokens = generatePathTokens(serviceManifest);
        InvocationImpl event = new InvocationImpl( );
        event.setSentence(pathTokens);
        event.setEvent(intent.getId());
        event.setEventValue(intent);
        return event;
    }



}
