package com.wrupple.muba.desktop;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.*;
import com.wrupple.muba.desktop.client.chain.command.impl.*;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.desktop.domain.LaunchWorkerManifest;
import com.wrupple.muba.desktop.domain.impl.ContextSwitchImpl;
import com.wrupple.muba.desktop.domain.impl.LaunchWorkerManifestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.worker.domain.WorkRequest;
import com.wrupple.muba.worker.domain.impl.ContainerStateImpl;
import com.wrupple.muba.worker.domain.impl.WorkRequestImpl;

public class WorkerModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(SwitchWorkerContext.class).to(SwitchWorkerContextImpl.class);

        bind(ContextSwitch.class).to(ContextSwitchImpl.class);
        bind(ContainerState.class).to(ContainerStateImpl.class);

        bind(LaunchWorkerEngine.class).to(LaunchWorkerEngineImpl.class);
        bind(LaunchWorkerInterpret.class).to(LaunchWorkerInterpretImpl.class);
        bind(LaunchWorkerManifest.class).to(LaunchWorkerManifestImpl.class);
        bind(ReadWorkerMetadata.class).to(ReadWorkerMetadataImpl.class);
        bind(StartWorkerHeartBeat.class).to(StartWorkerHeartBeatImpl.class);
        bind(CreateWorkerStructure.class).to(CreateWorkerStructureImpl.class);
        bind(InstallActivityEventHandler.class).to(InstallActivityEventHandlerImpl.class);

    }



}
