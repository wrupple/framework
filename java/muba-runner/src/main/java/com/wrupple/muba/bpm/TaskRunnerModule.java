package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.ActivityContextImpl;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.domain.impl.RunnerServiceManifestImpl;
import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.bpm.server.service.impl.TaskRunnerPluginImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/**
 * Created by rarl on 10/05/17.
 */
public class TaskRunnerModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(ActivityContext.class).to(ActivityContextImpl.class);

        bind(TaskRunnerEngine.class).to(TaskRunnerEngineImpl.class);

        bind(DetermineSolutionFieldsDomain.class).to(DetermineSolutionFieldsDomainImpl.class).in(Singleton.class);
        bind(SynthesizeSolutionEntry.class).to(SynthesizeSolutionEntryImpl.class);
        bind(SelectSolution.class).to(SelectSolutionImpl.class);

        bind(LoadTask.class).to(LoadTaskImpl.class).in(Singleton.class);
        bind(ActivityRequestInterpret.class).to(ActivityRequestInterpretImpl.class).in(Singleton.class);
        bind(RunnerServiceManifest.class).to(RunnerServiceManifestImpl.class).in(Singleton.class);
        bind(TaskRunnerPlugin.class).to(TaskRunnerPluginImpl.class).in(Singleton.class);
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
