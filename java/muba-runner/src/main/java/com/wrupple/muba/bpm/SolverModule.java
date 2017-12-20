package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.*;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.bpm.server.service.impl.ProcessManagerImpl;
import com.wrupple.muba.bpm.server.service.impl.SolverCatalogPluginImpl;
import com.wrupple.muba.bpm.server.service.impl.SolverImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.ImplicitSuscriptionMapper;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContentNode;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;

/**
 * Created by rarl on 10/05/17.
 */
public class SolverModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Solver.class).to(SolverImpl.class).in(Singleton.class);


        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class).in(Singleton.class);
        bind(SolveTask.class).to(SolveTaskImpl.class);

           /*
        API
         */
        bind(ProcessManager.class).to(ProcessManagerImpl.class);
        bind(ApplicationContext.class).to(ApplicationContextImpl.class);
        bind(ApplicationState.class).to(ApplicationStateImpl.class);

        //engine
        bind(SolverEngine.class).to(SolverEngineImpl.class);

        //solver commands
        bind(DetermineSolutionFieldsDomain.class).to(DetermineSolutionFieldsDomainImpl.class).in(Singleton.class);
        bind(SynthesizeSolutionEntry.class).to(SynthesizeSolutionEntryImpl.class);
        bind(SelectSolution.class).to(SelectSolutionImpl.class);
        //bpm bindings
        bind(EventSuscriptionMapper.class).to(ImplicitSuscriptionMapper.class);

        bind(ActivityRequestInterpret.class).to(ActivityRequestInterpretImpl.class).in(Singleton.class);
        bind(SolverServiceManifest.class).to(SolverServiceManifestImpl.class).in(Singleton.class);
        bind(SolverCatalogPlugin.class).to(SolverCatalogPluginImpl.class).in(Singleton.class);

        bind(String.class).annotatedWith(Names.named(Task.CATALOG)).toInstance("/static/img/task.png");
        bind(String.class).annotatedWith(Names.named(WruppleActivityAction.CATALOG)).toInstance("/static/img/action.png");
        bind(String.class).annotatedWith(Names.named(TaskToolbarDescriptor.CATALOG)).toInstance("/static/img/task-piece.png");

    }


    @Provides
    @Singleton
    @Inject
    @Named(Workflow.WORKFLOW_CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder, @Named(ServiceManifest.CATALOG) CatalogDescriptor serviceManifest) {
        CatalogDescriptor r = builder.fromClass(WorkflowImpl.class, Workflow.WORKFLOW_CATALOG, "Workflow",
                -900190, serviceManifest);

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

    @Provides @Singleton @Inject @Named(Task.CATALOG)
    public CatalogDescriptor task(
                                               CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(TaskImpl.class, Task.CATALOG, "Task",
                -790090, null);
        r.setClazz(TaskImpl.class);
        return r;
    }


    @Provides
    @Inject
    @Singleton
    @Named(WruppleActivityAction.CATALOG)
    public CatalogDescriptor action() {
        return new CatalogDescriptorImpl();//FIXME real definition
    }

    @Provides
    @Inject
    @Singleton
    @Named(TaskToolbarDescriptor.CATALOG)
    public CatalogDescriptor toolbar() {
        return new CatalogDescriptorImpl();//FIXME real definition
    }

}
