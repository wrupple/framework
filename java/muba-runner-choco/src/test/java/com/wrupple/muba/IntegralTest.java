package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.ChocoSolverModule;
import com.wrupple.muba.bpm.SolverModule;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;
import java.io.InputStream;
import java.io.OutputStream;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

public class IntegralTest extends AbstractTest{

    /*
	 * mocks
	 */

    protected WriteOutput mockWriter;

    protected EventSuscriptionMapper mockSuscriptor;

    protected ProcessManager mockProcessManager;

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
            mockProcessManager=mock(ProcessManager.class);
            mockWriter = mock(WriteOutput.class);
            mockLogger = mock(WriteAuditTrails.class);
            peerValue = mock(Host.class);
            mockSuscriptor = mock(EventSuscriptionMapper.class);
            bind(WriteAuditTrails.class).toInstance(mockLogger);
            bind(WriteOutput.class).toInstance(mockWriter);
            bind(EventSuscriptionMapper.class).toInstance(mockSuscriptor);
            bind(ProcessManager.class).toInstance(mockProcessManager);
			/*
			 * COMMANDS
			 */

            bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
            bind(CatalogFileUploadUrlHandlerTransaction.class)
                    .toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));

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

    public IntegralTest() {
        init(new IntegralTestModule(),
                new ChocoSolverTestModule(),
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
    protected void registerServices(Validator v, ValidationGroupProvider g, EventBus switchs) {
        CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
        switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));

        CatalogActionFilterManifest preService = injector.getInstance(CatalogActionFilterManifest.class);
        switchs.getIntentInterpret().registerService(preService, injector.getInstance(CatalogActionFilterEngine.class),injector.getInstance(CatalogActionFilterInterpret.class));

        CatalogIntentListenerManifest listenerManifest = injector.getInstance(CatalogIntentListenerManifest.class);
        switchs.getIntentInterpret().registerService(listenerManifest, injector.getInstance(CatalogEventHandler.class),injector.getInstance(CatalogEventInterpret.class));


        BroadcastServiceManifest broadcastManifest = injector.getInstance(BroadcastServiceManifest.class);
        switchs.getIntentInterpret().registerService(broadcastManifest, injector.getInstance(PublishEvents.class),injector.getInstance(BroadcastInterpret.class));


        /*
         * Solver
         */

        SolverServiceManifest solverServiceManifest = injector.getInstance(SolverServiceManifest.class);
        switchs.getIntentInterpret().registerService(solverServiceManifest, injector.getInstance(SolverEngine.class),injector.getInstance(ActivityRequestInterpret.class));


    }

    @Before
    public void setUp() throws Exception {
        expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(peerValue.getSubscriptionStatus()).andStubReturn(Host.STATUS_ONLINE);
        expect(mockSuscriptor.execute(anyObject(Context.class))).andStubReturn(Command.CONTINUE_PROCESSING);

        runtimeContext = injector.getInstance(RuntimeContext.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }


}