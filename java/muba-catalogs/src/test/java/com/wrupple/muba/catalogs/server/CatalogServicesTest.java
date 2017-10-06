package com.wrupple.muba.catalogs.server;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import com.wrupple.muba.catalogs.server.service.TriggerStorageStrategy;
import com.wrupple.muba.catalogs.server.service.impl.TriggerStorageStrategyImpl;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.CatalogTestModule;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import org.apache.commons.chain.Command;
import org.junit.Before;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;

public class CatalogServicesTest extends MubaTest {
	/*
	 * mocks
	 */

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	protected Host peerValue;

	protected DataCreationCommand mockCreate;

	protected DataQueryCommand mockQuery;

	protected DataReadCommand mockRead;

	protected DataWritingCommand mockwrite;

	protected DataDeleteCommand mockDelete;
	private EventSuscriptionMapper mockSuscriptor;


	class CatalogServicesTestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);
			bind(TriggerStorageStrategy.class).to(TriggerStorageStrategyImpl.class);

			// mocks
			mockWriter = mock(WriteOutput.class);
			mockLogger = mock(WriteAuditTrails.class);
			peerValue = mock(Host.class);
             mockCreate = mock(DataCreationCommand.class);
             mockQuery = mock(DataQueryCommand.class);
             mockRead = mock(DataReadCommand.class);
             mockwrite = mock(DataWritingCommand.class);
             mockDelete = mock(DataDeleteCommand.class);
			mockSuscriptor = mock(EventSuscriptionMapper.class);
			bind(EventSuscriptionMapper.class).toInstance(mockSuscriptor);

			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).toInstance(mockCreate);
			bind(DataQueryCommand.class).toInstance(mockQuery);
			bind(DataReadCommand.class).toInstance(mockRead);
			bind(DataWritingCommand.class).toInstance(mockwrite);
			bind(DataDeleteCommand.class).toInstance(mockDelete);


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
		public SessionContext sessionContext() {
			Session stakeHolderValue = createNiceMock(Session.class);

			return new SessionContextImpl(stakeHolderValue);
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

	public CatalogServicesTest() {
		init(new CatalogServicesTestModule(),new CatalogTestModule(), new SingleUserModule(), new CatalogModule(), new ApplicationModule());
	}

	@Override
	protected void registerServices( EventBus switchs) {
		CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
		switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));
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
