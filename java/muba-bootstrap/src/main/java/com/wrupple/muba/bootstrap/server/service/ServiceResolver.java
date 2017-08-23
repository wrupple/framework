package com.wrupple.muba.bootstrap.server.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by japi on 22/08/17.
 */
public interface ServiceResolver extends ImplicitEventResolver {


    public interface HandlerRegistration {
        void removeHandler();
    }

    HandlerRegistration addHandler(ServiceManifest serviceManifes);

    //FIXME fireEvent(event)

    List<String> resolveContractSentence(CatalogEntry serviceContract) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;


    RootServiceManifest getRootService();
    //contract interpret per type? bpm?
    void registerService(ServiceManifest manifest, Command service, RequestInterpret contractInterpret);

    void registerService(ServiceManifest manifest, Command service);

    /**
     * this is very much like widgetters/lookupcommands
     *
     * @param context
     * @return reads properties from context, aswell as context property depen
     */
    RequestInterpret getRequestInterpret(RuntimeContext context);

    CatalogFactory getDictionaryFactory();



}
