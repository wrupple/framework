package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.domain.impl.PersonImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.impl.EventRegistryImpl;
import com.wrupple.muba.worker.domain.*;
import com.wrupple.muba.event.domain.Intent;
import com.wrupple.muba.worker.domain.impl.*;
import com.wrupple.muba.worker.server.chain.BusinessEngine;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import com.wrupple.muba.worker.server.chain.WorkflowEngine;
import com.wrupple.muba.worker.server.chain.command.*;
import com.wrupple.muba.worker.server.chain.command.impl.*;
import com.wrupple.muba.worker.server.chain.impl.BusinessEngineImpl;
import com.wrupple.muba.worker.server.chain.impl.WorkflowEngineImpl;
import com.wrupple.muba.worker.server.domain.IntentResolverContextImpl;
import com.wrupple.muba.worker.server.service.BusinessPlugin;
import com.wrupple.muba.worker.server.service.impl.BusinessPluginImpl;

import java.util.Arrays;

import static com.wrupple.muba.event.domain.Intent.Intent_CATALOG;


/**
 * Created by japi on 11/08/17.
 */
public class BusinessModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("bpm.dictionary.outputHandler")).toInstance("mine");

        bind(Integer.class).annotatedWith(Names.named("com.wrupple.errors.unknownUser")).toInstance(0);


        /*
        Service
         */
        bind(BusinessServiceManifest.class).to(BusinessServiceManifestImpl.class);
        bind(IntentResolverServiceManifest.class).to(IntentResolverServiceManifestImpl.class);

        /*
        Context
         */

        bind(IntentResolverContext.class).to(IntentResolverContextImpl.class);

        /*
        Interprets
         */
        bind(BusinessRequestInterpret.class).to(BusinessRequestInterpretImpl.class);
        bind(IntentResolverRequestInterpret.class).to(IntentResolverRequestInterpretImpl.class);


        /*
        Engines
         */
        bind(IntentResolverEngine.class).to(IntentResolverEngineImpl.class);
        bind(BusinessEngine.class).to(BusinessEngineImpl.class);


        /*
        Commands
         */

        bind(WorkflowEngine.class).to(WorkflowEngineImpl.class);
        bind(CommitSubmission.class).to(CommitSubmissionImpl.class);
        bind(InferNextTask.class).to(InferNextTaskImpl.class);
        bind(UpdateApplicationContext.class).to(UpdateApplicationContextImpl.class);
        bind(ExplicitOutputPlace.class).to(ExplicitOutputPlaceImpl.class);
        bind(GoToCommand.class).to(GoToCommandImpl.class);
        bind(NextPlace.class).to(NextPlaceImpl.class);
        /*
        Services
         */

        bind(EventRegistry.class).to(EventRegistryImpl.class);
        bind(BusinessPlugin.class).to(BusinessPluginImpl.class);
        bind(StakeHolderTrigger.class).to(StakeHolderTriggerImpl.class);
        bind(ValueChangeAudit.class).to(ValueChangeAuditImpl.class);
        bind(ValueChangeListener.class).to(ValueChangeListenerImpl.class);
        /*
         *
         */
        bind(ApplicationState.class).to(ApplicationStateImpl.class);
        bind(Intent.class).to(IntentImpl.class);
        //a host can have many sessions that can have many application states
        bind(String.class).annotatedWith(Names.named(Host.CATALOG)).toInstance( "/static/img/session.png");

        bind(String.class).annotatedWith(Names.named(Application.CATALOG)).toInstance("/static/img/process.png");

        bind(String.class).annotatedWith(Names.named(Request.CATALOG)).toInstance("/static/img/notification.png");
        bind(String.class).annotatedWith(Names.named("catalog.storage.people")).toInstance(Person.CATALOG);

    }

    @Provides
    @Singleton
    @Inject
    @Named(Person.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder, @Named("catalog.storage.people") String catalogPluginStorage, @Named("catalog.storage") String defaultStorage) {
        CatalogDescriptor r = builder.fromClass(PersonImpl.class, Person.CATALOG,  Person.CATALOG,
                -13344556, null);
        r.setStorage(Arrays.asList(defaultStorage, catalogPluginStorage));

        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(Request.CATALOG)
    public CatalogDescriptor notification(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(RequestImpl.class, Request.CATALOG, "Request",
                -990091, null);
        r.setClazz(RequestImpl.class);
        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(Contract.Event_CATALOG)
    public CatalogDescriptor intent(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ContractImpl.class, Contract.Event_CATALOG, "Contract",
                -990093, null);
        r.setClazz(ContractImpl.class);
        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(Intent_CATALOG)
    public CatalogDescriptor commit(/*@Named(ManagedObject.MANAGED_CATALOG) CatalogDescriptor timeline,*/
                                         CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(IntentImpl.class, Intent_CATALOG, "Commit",
                -990095, null);
        r.setClazz(IntentImpl.class);
        return r;
    }




}
