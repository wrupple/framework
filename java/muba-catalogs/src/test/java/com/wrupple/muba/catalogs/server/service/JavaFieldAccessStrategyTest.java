package com.wrupple.muba.catalogs.server.service;

        import static org.easymock.EasyMock.anyObject;
        import static org.easymock.EasyMock.expect;
        import static org.junit.Assert.assertTrue;

        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.Arrays;
        import java.util.List;

        import javax.inject.Inject;
        import javax.inject.Named;
        import javax.inject.Singleton;
        import javax.transaction.UserTransaction;
        import javax.validation.Validator;

        import com.google.inject.Guice;
        import com.google.inject.Injector;
        import com.wrupple.muba.ValidationModule;
        import com.wrupple.muba.bootstrap.domain.*;
        import com.wrupple.muba.catalogs.CatalogTestModule;
        import com.wrupple.muba.catalogs.domain.*;
        import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
        import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
        import com.wrupple.muba.catalogs.server.chain.command.*;
        import com.wrupple.muba.catalogs.server.service.impl.JavaFieldAccessStrategy;
        import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
        import org.apache.commons.chain.Command;
        import org.easymock.EasyMockSupport;
        import org.junit.Before;
        import org.junit.Test;

        import com.google.inject.AbstractModule;
        import com.google.inject.Provides;
        import com.google.inject.name.Names;
        import com.wrupple.muba.MubaTest;
        import com.wrupple.muba.bootstrap.BootstrapModule;
        import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
        import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;
        import com.wrupple.muba.catalogs.CatalogModule;
        import com.wrupple.muba.catalogs.SingleUserModule;
        import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
        import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

public class JavaFieldAccessStrategyTest extends EasyMockSupport {
    protected Logger log = LoggerFactory.getLogger(JavaFieldAccessStrategyTest.class);

    private final Injector injector;
	/*
	 * mocks
	 */


    class JavaFieldAccessStrategyTestModule extends AbstractModule {

        CatalogPeer peerValue;
        @Override
        protected void configure() {
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

            // mocks
            WriteOutput mockWriter = mock(WriteOutput.class);
            WriteAuditTrails mockLogger = mock(WriteAuditTrails.class);
             peerValue= mock(CatalogPeer.class);
            EventSuscriptionChain chainMock = mock(EventSuscriptionChain.class);

            DataCreationCommand mockCreate = mock(DataCreationCommand.class);
            DataQueryCommand mockQuery = mock(DataQueryCommand.class);
            DataReadCommand mockRead = mock(DataReadCommand.class);
            DataWritingCommand mockwrite = mock(DataWritingCommand.class);
            DataDeleteCommand mockDelete = mock(DataDeleteCommand.class);
            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).toInstance(mockCreate);
            bind(DataQueryCommand.class).toInstance(mockQuery);
            bind(DataReadCommand.class).toInstance(mockRead);
            bind(DataWritingCommand.class).toInstance(mockwrite);
            bind(DataDeleteCommand.class).toInstance(mockDelete);


            bind(WriteAuditTrails.class).toInstance(mockLogger);
            bind(WriteOutput.class).toInstance(mockWriter);
            bind(EventSuscriptionChain.class).toInstance(chainMock);


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
        public CatalogDeserializationService catalogDeserializationService() {
            return mock(CatalogDeserializationService.class);
        }

        @Provides
        public Trash trash() {
            return mock(Trash.class);
        }
    }

    public JavaFieldAccessStrategyTest() {
        injector = Guice.createInjector(new JavaFieldAccessStrategyTestModule(), new CatalogTestModule(), new ValidationModule(),new SingleUserModule(), new CatalogModule(), new BootstrapModule());

    }

    @Before
    public void setUp() throws Exception {
       /* expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(chainMock.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(peerValue.getSubscriptionStatus()).andStubReturn(CatalogPeer.STATUS_ONLINE);

        runtimeContext = injector.getInstance(RuntimeContext.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");*/
    }


    @Test
    public void reflectionTest() throws Exception {

        FieldAccessStrategy access = injector.getInstance(JavaFieldAccessStrategy.class);
        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-reflection test-]");

        // expectations

        replayAll();

        CatalogDescriptor problemContract = builder.fromClass(TestEntry.class,"TestEntry","Test Catalog",-23l    ,null);

        TestEntry bean = (TestEntry) access.synthesize(problemContract);

        FieldAccessStrategy.Session session = access.newSession(bean);

        assertTrue(access.isWriteableProperty("number",bean,session));

        access.setPropertyValue("number",bean,7,session);

        Integer value = (Integer) access.getPropertyValue("number",bean,null,session);

        assertTrue(value.intValue()==7);

        problemContract.setClazz(PersistentCatalogEntity.class);

        HasAccesablePropertyValues map = (HasAccesablePropertyValues) access.synthesize(problemContract);

        access.setPropertyValue("number",map,7,session);

        value = (Integer) access.getPropertyValue("number",map,null,session);

        assertTrue(value.intValue()==7);


    }
}
