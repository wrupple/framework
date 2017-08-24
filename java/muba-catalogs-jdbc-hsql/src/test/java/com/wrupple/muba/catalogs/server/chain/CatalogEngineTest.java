package com.wrupple.muba.catalogs.server.chain;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.server.service.FormatDictionary;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.BootstrapModule;
import com.wrupple.muba.event.domain.SystemContext;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.domain.SessionContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCHSQLTestModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
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

	protected CatalogPeer peerValue;

	protected EventSuscriptionChain chainMock;

	class CatalogEngineTestModule extends AbstractModule {

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
			peerValue = mock(CatalogPeer.class);
			chainMock = mock(EventSuscriptionChain.class);
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

	public CatalogEngineTest() {
		init(new CatalogEngineTestModule(), new JDBCHSQLTestModule(), new HSQLDBModule(), new JDBCModule(),
				new ValidationModule(), new SingleUserModule(), new CatalogModule(), new BootstrapModule());
	}

	@Override
	protected void registerServices(Validator v, ValidationGroupProvider g, SystemContext switchs) {
		CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
		switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));
	}

	@Before
	public void setUp() throws Exception {
		expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(chainMock.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(peerValue.getSubscriptionStatus()).andStubReturn(CatalogPeer.STATUS_ONLINE);

		runtimeContext = injector.getInstance(RuntimeContext.class);
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
        FieldDescriptor solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue( solutionFieldDescriptor!= null);

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(problemContract);
        //action.setFollowReferences(true);
		runtimeContext.setServiceContract(action);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
		// locale is set in catalog
		runtimeContext.process();

		CatalogActionContext catalogContext = runtimeContext.getServiceContext();

		problemContract = catalogContext.getEntryResult();
		assertTrue(problemContract.getId() != null);
		assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
		solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
		assertTrue( solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);

		log.trace("[-see changes in catalog list-]");

		runtimeContext.reset();

		runtimeContext.setServiceContract(null);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogActionRequest.READ_ACTION);
		runtimeContext.process();
		catalogContext = runtimeContext.getServiceContext();
		assertTrue(catalogContext.getResults() != null);
		assertTrue(!catalogContext.getResults().isEmpty());
		boolean contained = false;

		for (CatalogEntry id : catalogContext.getResults()) {
			if(id.getId().equals(problemContract.getDistinguishedName())){
				contained = true ;
				break;
			}
		}

		assertTrue(contained);
		log.trace("[-see registered catalog Descriptor-]");
		runtimeContext.reset();

        catalogContext.setCatalog(MathProblem.class.getSimpleName());
        problemContract = catalogContext.getCatalogDescriptor();
        log.trace("[-verifying catalog graph integrity-]");
        assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
        solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue(solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);


		log.debug("-create math problem entry-");
		runtimeContext.reset();
		MathProblem problem = new MathProblem();
		problem.setName(MathProblem.class.getSimpleName());
		CatalogActionRequest contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID,
				problemContract.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, null, null, problem, null);
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, MathProblem.class.getSimpleName(),
				CatalogActionRequest.CREATE_ACTION);

		runtimeContext.process();

		problem = ((CatalogActionContext) runtimeContext.getServiceContext()).getEntryResult();
		assertTrue(problem.getId() != null);
		assertTrue(problem.getTimestamp() != null);

		log.debug("-check if child was created-");
		runtimeContext.reset();

		contract = new CatalogActionRequestImpl(CatalogEntry.PUBLIC_ID, ContentNode.CATALOG,
				CatalogActionRequest.READ_ACTION, null, null, null, FilterDataUtils.newFilterData());
		runtimeContext.setServiceContract(contract);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, ContentNode.CATALOG, CatalogActionRequest.READ_ACTION);

		runtimeContext.process();

		catalogContext = runtimeContext.getServiceContext();

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
	 * in general) i18n LocalizedEntityInterceptor security for unanted
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
