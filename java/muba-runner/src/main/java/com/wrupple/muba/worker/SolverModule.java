package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.chain.command.impl.ImplicitSuscriptionMapper;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.ApplicationDependencyImpl;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.reserved.HasChildren;
import com.wrupple.muba.event.domain.reserved.HasChildrenValues;
import com.wrupple.muba.event.domain.reserved.HasParent;
import com.wrupple.muba.event.domain.reserved.HasParentValue;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.domain.SolverServiceManifest;
import com.wrupple.muba.worker.domain.WruppleActivityAction;
import com.wrupple.muba.worker.domain.impl.*;
import com.wrupple.muba.worker.server.chain.SolverEngine;
import com.wrupple.muba.worker.server.chain.command.*;
import com.wrupple.muba.worker.server.chain.command.impl.*;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.Solver;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;
import com.wrupple.muba.worker.server.service.impl.ProcessManagerImpl;
import com.wrupple.muba.worker.server.service.impl.SolverCatalogPluginImpl;
import com.wrupple.muba.worker.server.service.impl.SolverImpl;

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
        bind(SolveTask.Callback.class).to(SolveTaskCallbackImpl.class);
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
    @Named(WorkerState.CATALOG)
    public CatalogDescriptor container(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(WorkerStateImpl.class, WorkerState.CATALOG, "Container",
                -900193, null);

        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(ApplicationDependency.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ApplicationDependencyImpl.class, ApplicationDependency.CATALOG, "Application dependency",
                -900195, null);

        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(Application.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder, @Named(ServiceManifest.CATALOG) CatalogDescriptor serviceManifest) {
        CatalogDescriptor r = builder.fromClass(ApplicationImpl.class, Application.CATALOG, "Application",
                -900190, serviceManifest);
        FieldDescriptor children = r.getFieldDescriptor(HasChildren.FIELD);
        children.setCatalog(Application.CATALOG);
        FieldDescriptor childValues = r.getFieldDescriptor(HasChildrenValues.FIELD);
        childValues.setCatalog(Application.CATALOG);
        FieldDescriptor parent = r.getFieldDescriptor(HasParent.FIELD);
        parent.setCatalog(Application.CATALOG);


        //FieldDescriptor parentValue = r.getFieldDescriptor(HasParentValue.VALUE_FIELD);
       // parentValue.setCatalog(Application.CATALOG);

        //FIXME not consolidated
        r.setConsolidated(true);
        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(ApplicationState.CATALOG)
    public CatalogDescriptor application(@Named(ManagedObject.CATALOG_TIMELINE) CatalogDescriptor timeline,
                                         CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ApplicationStateImpl.class, ApplicationState.CATALOG, "Thread",
                -990094, timeline);
        r.setClazz(ApplicationStateImpl.class);
        r.setConsolidated(true);

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
