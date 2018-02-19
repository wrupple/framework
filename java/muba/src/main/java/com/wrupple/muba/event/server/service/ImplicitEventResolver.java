package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by japi on 22/08/17.
 */
public interface ImplicitEventResolver {

    void registerService(Registration registration);

    List<ServiceManifest> resolveHandlers(String intentType);

    //FIXME fireHandler(event)

    List<String> resolveContractSentence(CatalogEntry serviceContract) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;


    ParentServiceManifest getRootService();

    Invocation resolveIntent(Contract intent, RuntimeContext context) throws Exception;

    //contract interpret per type? bpm?
    void registerService(ServiceManifest manifest, Command service, RequestInterpret contractInterpret);

    void registerService(ServiceManifest manifest, Command service);

    /**
     * this is very much like widgetters/lookupcommands
     *
     * @param context
     * @return reads properties from context, aswell as context property depen
     */
    RequestInterpret getExplicitIntentInterpret(RuntimeContext context);

    Invocation resolveIntent(Contract implicitRequestContract, ServiceManifest manifest, RuntimeContext parentTimeline);

    interface Registration {
        ServiceManifest getManifest();

        Command getService();

        RequestInterpret getContractInterpret();

        ParentServiceManifest getParent();
    }


    CatalogFactory getDictionaryFactory();


}
