package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
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
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import org.junit.Before;

import javax.transaction.UserTransaction;
import javax.validation.Validator;
import java.io.InputStream;
import java.io.OutputStream;

import static com.wrupple.muba.event.domain.SessionContext.SYSTEM;
import static org.easymock.EasyMock.expect;

public class IntegralTest extends AbstractTest{

    /*
	 * mocks
	 */



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

            peerValue = mock(Host.class);

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
        init(new IntegralTestModule(), new JDBCHSQLTestModule(), new HSQLDBModule(), new JDBCModule(), new SQLModule(),
                new ValidationModule(), new SingleUserModule(), new CatalogModule(), new ApplicationModule());
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


    }

    @Before
    public void setUp() throws Exception {
        expect(peerValue.getSubscriptionStatus()).andStubReturn(Host.STATUS_ONLINE);
        runtimeContext = new RuntimeContextImpl(injector.getInstance(EventBus.class),injector.getInstance( Key.get(SessionContext.class,Names.named(SYSTEM))));
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }


}
