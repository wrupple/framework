package com.wrupple.muba.catalogs.server;

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

import com.wrupple.muba.catalogs.CatalogTestModule;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.chain.command.*;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

public class CatalogServicesTest extends MubaTest {
	/*
	 * mocks
	 */

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	protected CatalogPeer peerValue;

	protected DataCreationCommand mockCreate;

	protected DataQueryCommand mockQuery;

	protected DataReadCommand mockRead;

	protected DataWritingCommand mockwrite;

	protected DataDeleteCommand mockDelete;

	protected EventSuscriptionChain chainMock;

	class CatalogServicesTestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

			// mocks
			mockWriter = mock(WriteOutput.class);
			mockLogger = mock(WriteAuditTrails.class);
			peerValue = mock(CatalogPeer.class);
			chainMock = mock(EventSuscriptionChain.class);
             mockCreate = mock(DataCreationCommand.class);
             mockQuery = mock(DataQueryCommand.class);
             mockRead = mock(DataReadCommand.class);
             mockwrite = mock(DataWritingCommand.class);
             mockDelete = mock(DataDeleteCommand.class);


			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).toInstance(mockCreate);
			bind(DataQueryCommand.class).toInstance(mockQuery);
			bind(DataReadCommand.class).toInstance(mockRead);
			bind(DataWritingCommand.class).toInstance(mockwrite);
			bind(DataDeleteCommand.class).toInstance(mockDelete);


			bind(WriteAuditTrails.class).toInstance(mockLogger);
			bind(WriteOutput.class).toInstance(mockWriter);
			bind(EventSuscriptionChain.class).toInstance(chainMock);
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

	public CatalogServicesTest() {
		init(new CatalogServicesTestModule(),new CatalogTestModule(), new SingleUserModule(), new CatalogModule(), new BootstrapModule());
	}

	@Override
	protected void registerServices( ApplicationContext switchs) {
		CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
		switchs.registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class));
		switchs.registerContractInterpret(catalogServiceManifest, injector.getInstance(CatalogRequestInterpret.class));
	}

	@Before
	public void setUp() throws Exception {
		expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(chainMock.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(peerValue.getSubscriptionStatus()).andStubReturn(CatalogPeer.STATUS_ONLINE);

		excecutionContext = injector.getInstance(ExcecutionContext.class);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}


}
