package com.wrupple.muba.worker.shared.services;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.CatalogActionFilterManifest;
import com.wrupple.muba.catalogs.domain.CatalogIntentListenerManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.Session;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.ImplicitEventResolver;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.event.server.service.impl.BroadcastEventHandlerImpl;
import com.wrupple.muba.worker.domain.BusinessServiceManifest;
import com.wrupple.muba.worker.domain.IntentResolverServiceManifest;
import com.wrupple.muba.worker.domain.SolverServiceManifest;
import com.wrupple.muba.worker.server.chain.BusinessEngine;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import com.wrupple.muba.worker.server.chain.SolverEngine;
import com.wrupple.muba.worker.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.worker.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.worker.server.chain.command.IntentResolverRequestInterpret;
import com.wrupple.muba.worker.server.service.Runner;
import com.wrupple.muba.worker.server.service.Solver;

import java.util.List;

public class WorkerContainer {

    private final ServiceBus processSwitches;
    private  SessionContext session;
    private final Solver solver;
    private final Injector injector;

    public WorkerContainer(List<Module> modules, List<Class<? extends ImplicitEventResolver.Registration>> eventHandlers) {


        injector = Guice.createInjector(modules);
        solver = injector.getInstance(Solver.class);
        processSwitches = injector.getInstance(ServiceBus.class);


/*
         Catalog
		 */

        CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
        processSwitches.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class), injector.getInstance(CatalogRequestInterpret.class));

        CatalogActionFilterManifest preService = injector.getInstance(CatalogActionFilterManifest.class);
        processSwitches.getIntentInterpret().registerService(preService, injector.getInstance(CatalogActionFilterEngine.class), injector.getInstance(CatalogActionFilterInterpret.class));

        CatalogIntentListenerManifest listenerManifest = injector.getInstance(CatalogIntentListenerManifest.class);
        processSwitches.getIntentInterpret().registerService(listenerManifest, injector.getInstance(CatalogEventHandler.class), injector.getInstance(CatalogEventInterpret.class));


		/*
		 BPM
		 */

        BusinessServiceManifest bpm = injector.getInstance(BusinessServiceManifest.class);

        processSwitches.getIntentInterpret().registerService(bpm, injector.getInstance(BusinessEngine.class), injector.getInstance(BusinessRequestInterpret.class));

        processSwitches.getIntentInterpret().registerService(injector.getInstance(IntentResolverServiceManifest.class), injector.getInstance(IntentResolverEngine.class), injector.getInstance(IntentResolverRequestInterpret.class));

        processSwitches.getIntentInterpret().registerService(injector.getInstance(BroadcastEventHandlerImpl.class));


        /*
          Solver
         */

        processSwitches.getIntentInterpret().registerService(injector.getInstance(SolverServiceManifest.class), injector.getInstance(SolverEngine.class), injector.getInstance(ActivityRequestInterpret.class));

        for (Class<? extends ImplicitEventResolver.Registration> registration : eventHandlers) {
            processSwitches.getIntentInterpret().registerService(injector.getInstance(registration));
        }

        /*
         * Security
         */

        session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM)));

    }


    public <T extends NaturalLanguageInterpret> void registerInterpret(String name, Class<T> interpret) {
        processSwitches.registerInterpret(name, injector.getInstance(interpret));

    }

    public <T extends Runner> void registerRunner(Class<T> runner) {
        solver.register(injector.getInstance(runner));
    }

    public <T> T  fireEvent(Contract contract) throws Exception {
        return processSwitches.fireEvent(contract, session, null);
    }

    public <T> T getInstance(Class<T> catalogDescriptorBuilderClass) {
        return injector.getInstance(catalogDescriptorBuilderClass);
    }

    public Injector getInjector() {
        return injector;
    }

    public ServiceBus getBus(){
        return processSwitches;
    }

    public SessionContext getSession(){
        return session;
    }

    public RuntimeContext newRuntime() {
        return new RuntimeContextImpl(getBus(),getSession());
    }
}