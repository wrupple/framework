package com.wrupple.muba.catalogs.server.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.CatalogTestModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.TestEntry;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.LegacyModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.impl.JavaFieldAccessStrategy;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertTrue;

public class JavaFieldAccessStrategyTest extends EasyMockSupport {
    protected Logger log = LogManager.getLogger(JavaFieldAccessStrategyTest.class);

    private final Injector injector;
	/*
	 * mocks
	 */


    class JavaFieldAccessStrategyTestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);
            // mocks


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




            bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
            bind(CatalogFileUploadUrlHandlerTransaction.class)
                    .toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));


        }


        @Provides
        public UserTransaction localTransaction() {
            return mock(UserTransaction.class);
        }


        @Provides
        public Trash trash() {
            return mock(Trash.class);
        }
    }

    public JavaFieldAccessStrategyTest() {
        injector = Guice.createInjector(new JavaFieldAccessStrategyTestModule(), new ValidationModule(), new SingleUserModule(),new CatalogTestModule(), new CatalogModule(), new LegacyModule(), new DispatcherModule(), new ApplicationModule());

    }

    @Before
    public void setUp() throws Exception {
       /* expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(chainMock.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(peerValue.getSubscriptionStatus()).andStubReturn(Host.STATUS_ONLINE);

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

        Instrospection instrospection = access.newSession(bean);

        assertTrue(access.isWriteableProperty("number",bean, instrospection));

        access.setPropertyValue("number",bean,7, instrospection);

        Integer value = (Integer) access.getPropertyValue("number",bean,null, instrospection);

        assertTrue(value.intValue()==7);

        problemContract.setClazz(PersistentCatalogEntity.class);

        HasAccesablePropertyValues map = (HasAccesablePropertyValues) access.synthesize(problemContract);
        access.setPropertyValue("number",map,7, instrospection);

        value = (Integer) access.getPropertyValue("number",map,null, instrospection);

        assertTrue(value.intValue()==7);


    }
}
