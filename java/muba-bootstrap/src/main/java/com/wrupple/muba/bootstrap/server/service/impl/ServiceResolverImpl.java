package com.wrupple.muba.bootstrap.server.service.impl;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;
import com.wrupple.muba.bootstrap.server.service.ServiceResolver;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by japi on 22/08/17.
 */
public class ServiceResolverImpl implements ServiceResolver {

    private final String DICTIONARY = RootServiceManifest.NAME + "-interpret";
    private final RootServiceManifest root;
    private final Map<String,ServiceManifest> serviceDictionary;

    private final CatalogFactory factory;
    @Inject
    public ServiceResolverImpl(RootServiceManifest root, CatalogFactory factory) {
        this.root = root;
        this.factory = factory;
        this.serviceDictionary=new HashMap<>();
    }


    public RootServiceManifest getRootService() {
        return root;
    }



    @Override
    public void registerService(ServiceManifest manifest, Command service, RequestInterpret contractInterpret) {
        registerService(manifest,service);
        registerContractInterpret(manifest,contractInterpret);
    }

    @Override
    public CatalogFactory getDictionaryFactory() {
        return factory;
    }

    private void registerContractInterpret(ServiceManifest manifest,
                                           RequestInterpret service) {
        Catalog dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
        if (dictionary == null) {
            dictionary = getDictionaryFactory().getCatalog(RootServiceManifest.NAME);
            if (dictionary == null) {
                dictionary = new CatalogBase();
                getDictionaryFactory().addCatalog(RootServiceManifest.NAME, dictionary);
            }
            dictionary = new CatalogBase();
            getDictionaryFactory().addCatalog(DICTIONARY, dictionary);

        }
        dictionary.addCommand(manifest.getServiceId(), service);

    }

    @Override
    public void registerService( ServiceManifest manifest, Command service) {
        Catalog dictionary = getDictionaryFactory().getCatalog(RootServiceManifest.NAME);
        if (dictionary == null) {
            dictionary = getDictionaryFactory().getCatalog(DICTIONARY);
            if (dictionary == null) {
                dictionary = new CatalogBase();
                getDictionaryFactory().addCatalog(DICTIONARY, dictionary);
            }

            dictionary = new CatalogBase();
            getDictionaryFactory().addCatalog(RootServiceManifest.NAME, dictionary);
        }
        dictionary.addCommand(manifest.getServiceId(), service);
        getRootService().register(manifest);
    }

    @Override
    public RequestInterpret getRequestInterpret(RuntimeContext req) {
        return (RequestInterpret) getDictionaryFactory().getCatalog(DICTIONARY).getCommand(req.getServiceManifest().getServiceId());
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


    @Override
    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception {
        String input = (String) intent.getCatalog();

        if(input!=null) {

            ServiceManifest serviceManifest = serviceDictionary.get(input);

            if (serviceManifest != null) {
                String output = intent.getOutputCatalog();
                if (output == null) {
                    return createIntent(context, intent, serviceManifest);
                }/*else if (output.equals(serviceManifest.getC)){
               FIXME support output type discrimination
            }*/
            }
        }
        return null;

    }

    private ExplicitIntent createIntent(RuntimeContext context, ImplicitIntent intent, ServiceManifest serviceManifest) {
        /* if application item extends service manifest this method can return explicit intents of any nature in the known tree, even desktop human tasks
         *
         *
         */
    }
}
