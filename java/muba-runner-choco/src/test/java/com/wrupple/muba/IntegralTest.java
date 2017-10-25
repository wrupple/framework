package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.ConstraintSolverModule;
import com.wrupple.muba.bpm.SolverModule;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.service.VariableConsensus;
import com.wrupple.muba.bpm.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.domain.CatalogActionFilterManifest;
import com.wrupple.muba.catalogs.domain.CatalogIntentListenerManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Session;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import org.junit.Before;

import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.OutputStream;

import static org.easymock.EasyMock.expect;

public class IntegralTest extends AbstractTest{


	/*
	 * mocks
	 */


    private Session stakeHolderValue;
    protected EventBus wrupple;



    class IntegralTestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(VariableConsensus.class).to(ArbitraryDesicion.class);
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

			/*
			 * COMMANDS
			 */

            bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
            bind(CatalogFileUploadUrlHandlerTransaction.class)
                    .toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));
            // TODO cms test isMasked FieldDescriptor

        }

        @Provides
        public UserTransaction localTransaction() {
            return mock(UserTransaction.class);
        }

        @Provides
        public Trash trash() {
            return mock(Trash.class);
        }

        @Provides
        public CatalogDeserializationService catalogDeserializationService() {
            return mock(CatalogDeserializationService.class);
        }

    }

    public IntegralTest() {
        init(new IntegralTestModule(),
                new ChocoSolverTestModule(),
                new ConstraintSolverModule(),
                new SolverModule(),
                new HSQLDBModule(),
                new JDBCModule(),
                new SQLModule(),
                new ValidationModule(),
                new SingleUserModule(),
                new CatalogModule(),
                new ApplicationModule());

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

        wrupple = injector.getInstance(EventBus.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }




}
