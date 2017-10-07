package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.impl.WorkflowEngineImpl;
import com.wrupple.muba.bpm.server.domain.IntentResolverContextImpl;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.bpm.server.service.impl.ProcessManagerImpl;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.*;
import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.IntentResolverEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.chain.impl.BusinessEngineImpl;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.bpm.server.service.impl.BusinessPluginImpl;
import com.wrupple.muba.event.server.service.impl.EventRegistryImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/**
 * Created by japi on 11/08/17.
 */
public class BusinessModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(Names.named("com.wrupple.errors.unknownUser")).toInstance(0);

        /*
        API
         */
        bind(ProcessManager.class).to(ProcessManagerImpl.class);
        /*
        Service
         */
        bind(BusinessServiceManifest.class).to(BusinessServiceManifestImpl.class);
        bind(IntentResolverServiceManifest.class).to(IntentResolverServiceManifestImpl.class);
        bind(WorkflowServiceManifest.class).to(WorkflowServiceManifestImpl.class);

        /*
        Context
         */

        bind(ApplicationContext.class).to(ApplicationContextImpl.class);
        bind(IntentResolverContext.class).to(IntentResolverContextImpl.class);

        /*
        Interprets
         */
        bind(BusinessRequestInterpret.class).to(BusinessRequestInterpretImpl.class);
        bind(IntentResolverRequestInterpret.class).to(IntentResolverRequestInterpretImpl.class);
        //used by BroadcastInterpretImpl
        bind(EventSuscriptionMapper.class).to(EventSuscriptionMapperImpl.class);

        /*
        Engines
         */
        bind(IntentResolverEngine.class).to(IntentResolverEngineImpl.class);
        bind(BusinessEngine.class).to(BusinessEngineImpl.class);
        bind(WorkflowEngine.class).to(WorkflowEngineImpl.class);

        /*
        Commands
         */
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
        bind(WorkCompleteEvent.class).to(WorkCompleteEventImpl.class);
        bind(BusinessIntent.class).to(BusinessIntentImpl.class);
    }

    @Provides
    @Singleton
    @Inject
    @Named(Workflow.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder, @Named(ServiceManifest.CATALOG) CatalogDescriptor serviceManifest) {
        CatalogDescriptor r = builder.fromClass(WorkflowImpl.class, Workflow.CATALOG, "Workflow",
                -900190, serviceManifest);

        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(WorkRequest.CATALOG)
    public CatalogDescriptor notification(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(WorkRequestImpl.class, WorkRequest.CATALOG, "WorkRequest",
                -990091, null);
        r.setClazz(WorkRequestImpl.class);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(WorkCompleteEvent.CATALOG)
    public CatalogDescriptor done(@Named(ContentNode.CATALOG_TIMELINE) CatalogDescriptor timeline,
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(WorkCompleteEventImpl.class, WorkCompleteEvent.CATALOG, "Work Completed",
                -990092, timeline);
        r.setClazz(WorkRequestImpl.class);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(ImplicitIntent.CATALOG)
    public CatalogDescriptor intent(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ImplicitIntentImpl.class, ImplicitIntent.CATALOG, "Event",
                -990093, null);
        r.setClazz(ImplicitIntentImpl.class);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(ApplicationState.CATALOG)
    public CatalogDescriptor application(@Named(ContentNode.CATALOG_TIMELINE) CatalogDescriptor timeline,
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ApplicationStateImpl.class, ApplicationState.CATALOG, "Thread",
                -990094, timeline);
        r.setClazz(ApplicationStateImpl.class);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(BusinessIntent.BusinessIntent_CATALOG)
    public CatalogDescriptor commit(@Named(ExplicitIntent.CATALOG) CatalogDescriptor timeline,
                                         CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(BusinessIntentImpl.class, BusinessIntent.BusinessIntent_CATALOG, "Commit",
                -990095, timeline);
        r.setClazz(BusinessIntentImpl.class);
        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(ExplicitIntent.CATALOG)
    public CatalogDescriptor explicit(
                                    CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ExplicitIntentImpl.class, ExplicitIntent.CATALOG, "Do",
                -990096, null);
        r.setClazz(ExplicitIntentImpl.class);
        return r;
    }



}
