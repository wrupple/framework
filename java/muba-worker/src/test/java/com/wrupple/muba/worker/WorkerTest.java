package com.wrupple.muba.worker;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.domain.CatalogActionFilterManifest;
import com.wrupple.muba.catalogs.domain.CatalogIntentListenerManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.WorkerModule;
import com.wrupple.muba.desktop.client.service.LaunchWorkerHandler;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.ContextSwitchHandler;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import com.wrupple.muba.event.server.service.impl.NaturalLanguageInterpretImpl;
import com.wrupple.muba.worker.domain.BusinessServiceManifest;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.IntentResolverServiceManifest;
import com.wrupple.muba.worker.domain.SolverServiceManifest;
import com.wrupple.muba.worker.server.chain.BusinessEngine;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import com.wrupple.muba.worker.server.chain.SolverEngine;
import com.wrupple.muba.worker.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.worker.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.worker.server.chain.command.IntentResolverRequestInterpret;
import com.wrupple.muba.worker.server.service.*;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.worker.server.service.impl.CatalogRunnerImpl;
import com.wrupple.muba.worker.server.service.impl.ChocoInterpret;
import com.wrupple.muba.worker.shared.services.WorkerContainer;
import com.wrupple.vegetate.VegetateCatalogsModule;
import com.wrupple.vegetate.VegetateModule;
import com.wrupple.vegetate.server.service.VegetateCatalogPlugin;
import com.wrupple.vegetate.service.RemoteBroadcastHandler;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

public abstract class WorkerTest extends EasyMockSupport {

    WorkerContainer container;



    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Injector injector;


    public WorkerTest() {

        List<Module> modules = Arrays.asList(
                (Module)new IntegralTestModule(),
                new WorkerModule(),
                new BusinessModule(),
                new ConstraintSolverModule(),
                new SolverModule(),
                new SimpleDatabaseModule(null),
                new HSQLDBModule(null),
                new JDBCModule(),
                new SQLModule(),
                new ValidationModule(),
                new SingleUserModule(),
                new VegetateCatalogsModule(),
                new CatalogModule(),
                new LambdaModule(),
                new VegetateModule(),
                new DispatcherModule(),
                new ApplicationModule()
        );

        /*TODO imitate explicitOutputPlace test method in SubmitToApplicationTest to create event handlers to launch a worker*/
        List handlers = Arrays.asList(
                ContextSwitchHandler.class,
                WorkerRequestHandler.class,
                LaunchWorkerHandler.class,
                RemoteBroadcastHandler.class

        );

        container= new WorkerContainer(modules, handlers);

        container.registerInterpret(Constraint.EVALUATING_VARIABLE, ChocoInterpret.class);
        container.registerInterpret(WorkerRequestManifest.NAME, ExplicitIntentInterpret.class);
        container.registerInterpret(NaturalLanguageInterpretImpl.ASSIGNATION, NaturalLanguageInterpret.class);

        container.registerRunner(ChocoRunner.class);
        container.registerRunner(CatalogRunner.class);

        injector = Guice.createInjector(modules);
        	/*
		 Catalog
		 */
        ServiceBus	switchs = injector.getInstance(ServiceBus.class);

        CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
        switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));

        CatalogActionFilterManifest preService = injector.getInstance(CatalogActionFilterManifest.class);
        switchs.getIntentInterpret().registerService(preService, injector.getInstance(CatalogActionFilterEngine.class),injector.getInstance(CatalogActionFilterInterpret.class));

        CatalogIntentListenerManifest listenerManifest = injector.getInstance(CatalogIntentListenerManifest.class);
        switchs.getIntentInterpret().registerService(listenerManifest, injector.getInstance(CatalogEventHandler.class),injector.getInstance(CatalogEventInterpret.class));

		/*
		 Vegetate
		 */

        BroadcastServiceManifest broadcastManifest = injector.getInstance(BroadcastServiceManifest.class);
        switchs.getIntentInterpret().registerService(broadcastManifest, injector.getInstance(PublishEvents.class),injector.getInstance(BroadcastInterpret.class));

		/*
		 BPM
		 */

        BusinessServiceManifest bpm = injector.getInstance(BusinessServiceManifest.class);

        switchs.getIntentInterpret().registerService(bpm, injector.getInstance(BusinessEngine.class), injector.getInstance(BusinessRequestInterpret.class));

        switchs.getIntentInterpret().registerService(injector.getInstance(IntentResolverServiceManifest.class), injector.getInstance(IntentResolverEngine.class), injector.getInstance(IntentResolverRequestInterpret.class));


        /*
          Solver
         */

        switchs.getIntentInterpret().registerService(injector.getInstance(SolverServiceManifest.class), injector.getInstance(SolverEngine.class), injector.getInstance(ActivityRequestInterpret.class));


    }

    protected Logger log = LogManager.getLogger(WorkerTest.class);



    protected void createMockDrivers() throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) container.getInstance(CatalogDescriptorBuilder.class).fromClass(Driver.class, Driver.CATALOG,
                "Driver", 0, null);
        solutionContract.setId(null);
        solutionContract.setConsolidated(true);
        CatalogCreateRequestImpl catalogActionRequest = new CatalogCreateRequestImpl(solutionContract, CatalogDescriptor.CATALOG_ID);
        catalogActionRequest.setFollowReferences(true);
        container.fireEvent(catalogActionRequest);
        Driver driver;


        long[] LOCATIONS = new long[]{20l, 7l, 2l, 20l, 30l};
        int NUM_DRIVERS = LOCATIONS.length;
        for (int i = 0; i < NUM_DRIVERS; i++) {
            driver = new Driver();
            //thus, best driver will have a location of 6, or 8 because 7 will not be available
            driver.setLocation(LOCATIONS[i]);

            driver.setAvailable(i % 2 == 0);

            catalogActionRequest = new CatalogCreateRequestImpl(driver, Driver.CATALOG);
            container.fireEvent(catalogActionRequest);

        }
    }


    static class IntegralTestModule extends AbstractModule {


        @Override
        protected void configure() {

            bind(String.class).annotatedWith(Names.named("worker.intialTitle")).toInstance("..::Desktop::..");
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.choco")).toInstance(1l);
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.catalog")).toInstance(2l);

            bind(VariableConsensus.class).to(ArbitraryDesicion.class);

            bind(CatalogRunner.class).to(CatalogRunnerImpl.class);
        }

        @Provides
        @Inject
        @Singleton
        @Named("catalog.plugins")
        public Object plugins(VegetateCatalogPlugin vegetate,SolverCatalogPlugin /* this is what makes it purr */ runner, BusinessPlugin bpm, SystemCatalogPlugin system) {
            CatalogPlugin[] plugins = new CatalogPlugin[]{system,bpm, runner,vegetate};
            return plugins;
        }


        @Provides
        public ProcessWindow queryRunner() {
            return new ProcessWindow() {
                @Override
                public TaskContainer getRootTaskPresenter() {
                    return null;
                }
            };
        }

    }


}
