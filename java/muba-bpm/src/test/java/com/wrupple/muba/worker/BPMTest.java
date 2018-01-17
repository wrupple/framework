package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.domain.CatalogActionFilterManifest;
import com.wrupple.muba.catalogs.domain.CatalogIntentListenerManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
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
import com.wrupple.muba.worker.server.service.VariableConsensus;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import org.junit.Before;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class BPMTest extends AbstractTest {

    protected EventBus wrupple;
	protected SessionContext session;

    protected void createMockDrivers() throws Exception {
		CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) injector.getInstance(CatalogDescriptorBuilder.class).fromClass(Driver.class, Driver.CATALOG,
				"Driver", 0, null);
		solutionContract.setId(null);
		solutionContract.setConsolidated(true);
		CatalogCreateRequestImpl catalogActionRequest = new CatalogCreateRequestImpl(solutionContract, CatalogDescriptor.CATALOG_ID);
		catalogActionRequest.setFollowReferences(true);
		wrupple.fireEvent(catalogActionRequest,session,null);
		Driver driver;
		for(long i = 0 ; i < 10 ; i++){
			driver = new Driver();
			//thus, best driver will have a location of 6, or 8 because 7 will not be available
			driver.setLocation(i);
			driver.setAvailable(i%2==0);

            catalogActionRequest= new CatalogCreateRequestImpl(driver,Driver.CATALOG);
            wrupple.fireEvent(catalogActionRequest,session,null);

		}
	}


	public BPMTest() {
		init(new IntegralTestModule(),
                new BPMTestModule(),
                new BusinessModule(),
                new ConstraintSolverModule(),
                new SolverModule(),
                new HSQLDBModule(),
                new JDBCModule(),
                new SQLModule(),
                new ValidationModule(),
                new SingleUserModule(),
                new CatalogModule(),
                new LambdaModule(),
                new DispatcherModule(),
                new ApplicationModule());

	}

    class IntegralTestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(VariableConsensus.class).to(ArbitraryDesicion.class);
            bind(Boolean.class).annotatedWith(Names.named("event.parallel")).toInstance(false);
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
            bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
            bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
            bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
            bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);


        }


    }

	@Override
	protected void registerServices(EventBus switchs) {
		/*
		 Catalog
		 */

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
/*
 BusinessServiceManifest bpm = injector.getInstance(BusinessServiceManifest.class);

        switchs.registerService(bpm, injector.getInstance(BusinessEngine.class), injector.getInstance(BusinessRequestInterpret.class));

        WorkflowServiceManifest taskManger = injector.getInstance(WorkflowServiceManifest.class);

        switchs.registerService(taskManger, injector.getInstance(WorkflowEngine.class), injector.getInstance(WorkflowEventInterpret.class),bpm);

        switchs.registerService(injector.getInstance(IntentResolverServiceManifest.class), injector.getInstance(IntentResolverEngine.class), injector.getInstance(IntentResolverRequestInterpret.class));

        switchs.registerService(injector.getInstance(CatalogServiceManifest.class), injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));

        switchs.registerService(injector.getInstance(SolverServiceManifest.class), injector.getInstance(SolverEngine.class), injector.getInstance(ActivityRequestInterpret.class));

 */

    }

	@Before
	public void setUp() throws Exception {

		session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM)));
		wrupple = injector.getInstance(EventBus.class);
        log.trace("NEW TEST EXCECUTION ENVIROMENT READY");
    }





}
