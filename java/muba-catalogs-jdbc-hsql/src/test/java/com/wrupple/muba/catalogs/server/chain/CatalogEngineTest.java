package com.wrupple.muba.catalogs.server.chain;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCHSQLTestModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.ContentNodeImpl;
import com.wrupple.muba.catalogs.domain.MathProblem;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataDeleteCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataReadCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataCreationCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataDeleteCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataQueryCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataWritingCommandImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

public class CatalogEngineTest extends MubaTest {
	/*
	 * mocks
	 */

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	class CatalogEngineTestModule extends AbstractModule {

		@Override
		protected void configure() {

			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
			bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
			bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
			bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
			bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

			// mocks
			mockWriter = mock(WriteOutput.class);
			mockLogger = mock(WriteAuditTrails.class);
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
			Host peerValue = mock(Host.class);
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

	public CatalogEngineTest() {
		init(new CatalogEngineTestModule(), new JDBCModule(), new JDBCHSQLTestModule(), new HSQLDBModule(),
				new SingleUserModule(), new CatalogModule(), new BootstrapModule());
	}

	@Before
	public void setUp() throws Exception {

		expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		excecutionContext = injector.getInstance(ExcecutionContext.class);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}

	/**
	 * <ol>
	 * <li>create math problem catalog (with inheritance)</li>
	 * <li></li>
	 * <li></li>
	 * </ol>
	 * 
	 * @throws Exception
	 */
	@Test
	public void engineTest() throws Exception {

		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		log.trace("[-create catalog-]");

		// expectations

		replayAll();

		CatalogDescriptor problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
				"Math Problem", 0, builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG,
						ContentNode.class.getSimpleName(), -1l, null));

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(problemContract);

		excecutionContext.setServiceContract(action);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

		excecutionContext.process();

		CatalogActionContext catalogContext = excecutionContext.getServiceContext();

		problemContract = catalogContext.getEntryResult();
		assertTrue(problemContract.getId() != null);
		assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));

		log.trace("[-see changes in catalog list-]");

		excecutionContext.reset();

		excecutionContext.setServiceContract(null);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogActionRequest.LIST_ACTION_TOKEN);
		excecutionContext.process();
		catalogContext = excecutionContext.getServiceContext();
		Object raw = catalogContext.get(CatalogResultSet.MULTIPLE_FOREIGN_KEY);
		assertTrue(raw != null);
		assertTrue(raw instanceof List);
		log.trace("[-see registered catalog Descriptor-]");

		excecutionContext.reset();

		excecutionContext.setServiceContract(null);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogActionRequest.LIST_ACTION_TOKEN,
				MathProblem.class.getSimpleName());

		excecutionContext.process();

		catalogContext = excecutionContext.getServiceContext();
		raw = catalogContext.get(CatalogResultSet.MULTIPLE_FOREIGN_KEY);
		assertTrue(raw != null);
		assertTrue(raw instanceof CatalogDescriptor);

		log.debug("-create math problem entry-");
		excecutionContext.reset();
		MathProblem problem = new MathProblem();
		problem.setName(MathProblem.class.getSimpleName());
		problem.setStatement(Arrays.asList("do", "this"));
		CatalogActionRequest contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID,
				problemContract.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, null, null, problem, null);
		excecutionContext.setServiceContract(contract);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, MathProblem.class.getSimpleName(),
				CatalogActionRequest.CREATE_ACTION);

		excecutionContext.process();

		problem = ((CatalogActionContext) excecutionContext.getServiceContext()).getEntryResult();
		assertTrue(problem.getId() != null);
		assertTrue(problem.getTimestamp() != null);

		log.debug("-check if child was created-");
		excecutionContext.reset();

		contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID, ContentNode.CATALOG,
				CatalogActionRequest.READ_ACTION, null, null, null, FilterDataUtils.newFilterData());
		excecutionContext.setServiceContract(contract);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, ContentNode.CATALOG, CatalogActionRequest.READ_ACTION);

		excecutionContext.process();

		catalogContext = excecutionContext.getServiceContext();

		assertTrue(catalogContext.getResults() != null);
		assertTrue(catalogContext.getResults().size() == 1);
		assertTrue(catalogContext.getResults().get(0).getName().equals(problem.getName()));

	}
	// DROP COLUMN/INDEX CatalogDescriptorUpdateTriggerImpl

		// ADD COLUMN/INDEX
		// pluggable! FIXME delete all catalogs of a domain when domain is dropped
		// FIXME Clean entities with no corresponding catalog in namespace
		// https://cloud.google.com/appengine/docs/java/datastore/metadataqueries?csw=1#Namespace_Queries
	/*
	 * @Test is multiple delete handles by triggers gracefully (batch processes
	 * in general)  i18n LocalizedEntityInterceptor security for unanted
	 * crossdomain access by querying for ids trash, restore, dump public
	 * timeline
	 * 
	 * transactiondemarcation() { 
	 * 
	 * @Test public void vanityId() { fail("Not yet implemented"); }
	 * 
	 * @Test public void distributedLocalization() { fail("Not yet implemented"
	 * ); }
	 * 
	 * @Test public void centralizedLocalization() { fail("Not yet implemented"
	 * ); }
	 * 
	 * 
	 * @Test public void hardKeys() { fail("Not yet implemented"); }
	 */

}
