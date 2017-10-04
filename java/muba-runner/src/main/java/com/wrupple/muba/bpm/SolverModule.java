package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.ApplicationContextImpl;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.domain.impl.SolverServiceManifestImpl;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.bpm.server.service.impl.SolverCatalogPluginImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/**
 * Created by rarl on 10/05/17.
 */
public class SolverModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApplicationContext.class).to(ApplicationContextImpl.class);

        bind(SolverEngine.class).to(SolverEngineImpl.class);

        bind(DetermineSolutionFieldsDomain.class).to(DetermineSolutionFieldsDomainImpl.class).in(Singleton.class);
        bind(SynthesizeSolutionEntry.class).to(SynthesizeSolutionEntryImpl.class);
        bind(SelectSolution.class).to(SelectSolutionImpl.class);

        bind(LoadTask.class).to(LoadTaskImpl.class).in(Singleton.class);
        bind(ActivityRequestInterpret.class).to(ActivityRequestInterpretImpl.class).in(Singleton.class);
        bind(SolverServiceManifest.class).to(SolverServiceManifestImpl.class).in(Singleton.class);
        bind(SolverCatalogPlugin.class).to(SolverCatalogPluginImpl.class).in(Singleton.class);
    }

    @Provides @Singleton @Inject @Named(ProcessTaskDescriptor.CATALOG)
    public CatalogDescriptor task(
                                               CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ProcessTaskDescriptorImpl.class, ProcessTaskDescriptor.CATALOG, "Task",
                -790090, null);
        r.setClazz(ProcessTaskDescriptorImpl.class);
        return r;
    }


    @Provides
    @Inject
    @Singleton
    @Named(WruppleActivityAction.CATALOG)
    public CatalogDescriptor action() {
        return null;
    }

    @Provides
    @Inject
    @Singleton
    @Named(TaskToolbarDescriptor.CATALOG)
    public CatalogDescriptor toolbar() {
        return null;
    }

}
