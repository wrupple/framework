package com.wrupple.muba.bpm;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bpm.domain.BusinessServiceManifest;
import com.wrupple.muba.bpm.domain.IntentResolverServiceManifest;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.bpm.domain.WorkflowServiceManifest;
import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.IntentResolverEngine;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.IntentResolverRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.WorkflowEventInterpret;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.OutputStream;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

public abstract class BPMTest extends AbstractTest {

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
    }

    protected Logger log = LoggerFactory.getLogger(BPMTest.class);

	/*
	 * mocks
	 */

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	protected Host peerValue;


	class IntegralTestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
			bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
			bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
			bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
			bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

			// mocks
			mockWriter = mock(WriteOutput.class);
			mockLogger = mock(WriteAuditTrails.class);
			peerValue = mock(Host.class);
			bind(WriteAuditTrails.class).toInstance(mockLogger);
			bind(WriteOutput.class).toInstance(mockWriter);

			/*
			 * COMMANDS
			 */

			bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
			bind(CatalogFileUploadUrlHandlerTransaction.class)
					.toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));
			// TODO cms test isMasked FieldDescriptor

		}

		@Provides
		@Inject
		@Singleton
		public SessionContext sessionContext(@Named("host") String peer) {
			long stakeHolder = 1;
			Person stakeHolderValue = mock(Person.class);

			return new SessionContextImpl(stakeHolder, stakeHolderValue, peer, peerValue, CatalogEntry.PUBLIC_ID);
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

	public BPMTest() {
		init(new IntegralTestModule(),
                new BPMTestModule(),
                new BusinessModule(),
                new ChocoSolverModule(),
                new SolverModule(),
                new HSQLDBModule(),
                new JDBCModule(),
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
		 BPM
		 */

        BusinessServiceManifest bpm = injector.getInstance(BusinessServiceManifest.class);

        switchs.getIntentInterpret().registerService(bpm, injector.getInstance(BusinessEngine.class), injector.getInstance(BusinessRequestInterpret.class));

        WorkflowServiceManifest taskManger = injector.getInstance(WorkflowServiceManifest.class);

        switchs.getIntentInterpret().registerService(taskManger, injector.getInstance(WorkflowEngine.class), injector.getInstance(WorkflowEventInterpret.class),bpm);

        switchs.getIntentInterpret().registerService(injector.getInstance(IntentResolverServiceManifest.class), injector.getInstance(IntentResolverEngine.class), injector.getInstance(IntentResolverRequestInterpret.class));

        switchs.getIntentInterpret().registerService(injector.getInstance(CatalogServiceManifest.class), injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));

        /*
          Solver
         */

        switchs.getIntentInterpret().registerService(injector.getInstance(SolverServiceManifest.class), injector.getInstance(SolverEngine.class), injector.getInstance(ActivityRequestInterpret.class));


    }

	@Before
	public void setUp() throws Exception {
		expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(peerValue.getSubscriptionStatus()).andStubReturn(Host.STATUS_ONLINE);

		runtimeContext = injector.getInstance(RuntimeContext.class);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}





}
