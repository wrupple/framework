package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.domain.CatalogActionFilterManifest;
import com.wrupple.muba.catalogs.domain.CatalogIntentListenerManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Session;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import com.wrupple.muba.worker.ConstraintSolverModule;
import com.wrupple.muba.worker.SolverModule;
import com.wrupple.muba.worker.domain.SolverServiceManifest;
import com.wrupple.muba.worker.server.chain.SolverEngine;
import com.wrupple.muba.worker.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.worker.server.service.VariableConsensus;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import org.junit.Before;

import java.io.InputStream;
import java.io.OutputStream;

import static org.easymock.EasyMock.expect;

public class IntegralTest extends AbstractTest{


	/*
	 * mocks
	 */


    private Session stakeHolderValue;
    protected ServiceBus wrupple;


    public IntegralTest() {
        init(new IntegralTestModule(),
                new ChocoSolverTestModule(),
                new ConstraintSolverModule(),
                new SolverModule(),
                new HSQLDBModule(null),
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
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.choco")).toInstance(2l);
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
            bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
            bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
            bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
            bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

            // mocks
            stakeHolderValue = mock(Session.class);


        }


    }

    @Override
    protected void registerServices(ServiceBus switchs) {
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
        expect(stakeHolderValue.getDomain()).andStubReturn(CatalogEntry.PUBLIC_ID);
        expect(stakeHolderValue.getId()).andStubReturn(CatalogEntry.PUBLIC_ID);

        wrupple = injector.getInstance(ServiceBus.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }




}
