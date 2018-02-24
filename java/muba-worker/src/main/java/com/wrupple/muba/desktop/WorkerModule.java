package com.wrupple.muba.desktop;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.client.chain.ContextSwitchEngine;
import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.WorkerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.*;
import com.wrupple.muba.desktop.client.chain.command.impl.*;
import com.wrupple.muba.desktop.client.service.LaunchWorkerHandler;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.client.service.impl.ContextSwitchHandlerImpl;
import com.wrupple.muba.desktop.client.service.impl.LaunchWorkerHandlerImpl;
import com.wrupple.muba.desktop.client.service.impl.WorkerRequestHandlerImpl;
import com.wrupple.muba.desktop.domain.*;
import com.wrupple.muba.desktop.domain.impl.*;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.worker.domain.impl.WorkerStateImpl;

public class WorkerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ContainerContext.class).to(ContainerContextImpl.class);
        bind(ContextSwitchHandler.class).to(ContextSwitchHandlerImpl.class);
        bind(WorkerRequestHandler.class).to(WorkerRequestHandlerImpl.class);

        bind(ContextSwitchRuntimeContext.class).to(ContextSwitchRuntimeContextImpl.class);
        bind(ContextSwitch.class).to(ContextSwitchImpl.class);
        bind(WorkerRequestContext.class).to(WorkerRequestContextImpl.class);
        bind(ContextSwitchManifest.class).to(ContextSwitchManifestImpl.class);
        bind(WorkerRequestManifest.class).to(WorkerRequestManifestImpl.class);
        bind(WorkerState.class).to(WorkerStateImpl.class);

        bind(ApplicationStateListener.class).to(ApplicationStateListenerImpl.class);
        bind(BindHost.class).to(BindHostImpl.class);
        bind(PopulateLoadOrder.class).to(PopulateLoadOrderImpl.class);
        bind(BindApplication.class).to(BindApplicationImpl.class);
        bind(DesktopWriterCommand.class).to(DesktopWriterCommandImpl.class);
        bind(WorkerRequestInterpret.class).to(WorkerRequestInterpretImpl.class);
        bind(BuildApplicationTree.class).to(BuildApplicationTreeImpl.class);
        bind(DeclareDependencies.class).to(DeclareDependenciesImpl.class);
        bind(WorkerRequestEngine.class).to(WorkerRequestEngineImpl.class);
        bind(HandleContainerState.class).to(HandleContainerStateImpl.class);
        bind(ImportResources.class).to(ImportResourcesImpl.class);
        bind(ImportResourcesCallback.class).to(ImportResourcesCallbackImpl.class);
        bind(SwitchWorkerContext.class).to(SwitchWorkerContextImpl.class);
        bind(ContextSwitchInterpret.class).to(ContextSwitchInterpretImpl.class);
        bind(ContextSwitchEngine.class).to(ContextSwitchEngineImpl.class);
        bind(LaunchWorkerEngine.class).to(LaunchWorkerEngineImpl.class);
        bind(LaunchWorkerInterpret.class).to(LaunchWorkerInterpretImpl.class);
        bind(LaunchWorkerManifest.class).to(LaunchWorkerManifestImpl.class);
        bind(LaunchWorkerHandler.class).to(LaunchWorkerHandlerImpl.class);
        bind(ReadWorkerMetadata.class).to(ReadWorkerMetadataImpl.class);
        bind(StartWorkerHeartBeat.class).to(StartWorkerHeartBeatImpl.class);
        bind(CreateWorkerStructure.class).to(CreateWorkerStructureImpl.class);
        bind(InstallActivityEventHandler.class).to(InstallActivityEventHandlerImpl.class);

    }


    @Provides
    @Singleton
    @Inject
    @Named(ContextSwitch.CATALOG)
    public CatalogDescriptor contextSwitch(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ContextSwitchImpl.class, ContextSwitch.CATALOG, "Context Switch",
                -700160, null);

        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(WorkerContract.CATALOG)
    public CatalogDescriptor request(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(WorkerContractImpl.class, WorkerContract.CATALOG, "Worker Request",
                -700161, null);

        return r;
    }
}
