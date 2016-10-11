package com.wrupple.muba.catalogs.server.chain.command;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.ParentServiceManifest;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCHSQLTestModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.Argument;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataCreationCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataDeleteCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataQueryCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataWritingCommandImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogManager;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogDescriptorBuilderImpl;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.impl.UserCatalogPluginImpl;
import com.wrupple.muba.catalogs.shared.services.CatalogEvaluationService;

public class HSQLTest extends MubaTest {

	private CatalogDescriptorBuilderImpl builder;

	public HSQLTest() {
		builder = new CatalogDescriptorBuilderImpl();
		init(new CRUDModule(), new CatalogModule(), new SingleUserModule(), new JDBCHSQLTestModule(),
				new HSQLDBModule(), new JDBCModule(), new BootstrapModule());

	
	}

	/*
	 * mocks
	 */

	private class CRUDModule extends AbstractModule {

		@Override
		protected void configure() {

			bind(Integer.class).annotatedWith(Names.named("catalog.missingTableErrorCode")).toInstance(
					org.hsqldb.error.ErrorCode.X_42501 * -1/* 1146 in MySQL */);

			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
			bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
			bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
			bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
			bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);
			// no intercepting necessary
			bind(CatalogReaderInterceptor.class).to(NonOperativeCatalogReaderInterceptor.class);

			/*
			 * garbage dependencies
			 */
			bind(ParentServiceManifest.class).annotatedWith(Names.named("bootstrap.seoAwareService"))
					.toInstance(mock(ParentServiceManifest.class));
			bind(UserCatalogPlugin.class).to(UserCatalogPluginImpl.class);
			bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
			bind(CatalogFileUploadUrlHandlerTransaction.class)
					.toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));

			ValidateUserData mockValidator = mock(ValidateUserData.class);
			WriteOutput mockWriter = mock(WriteOutput.class);
			WriteAuditTrails mockLogger = mock(WriteAuditTrails.class);
			PublishEvents mockPublisher = mock(PublishEvents.class);

			bind(PublishEvents.class).toInstance(mockPublisher);
			bind(WriteAuditTrails.class).toInstance(mockLogger);
			bind(WriteOutput.class).toInstance(mockWriter);
			bind(ValidateUserData.class).toInstance(mockValidator);

		}

		@Provides
		@Inject
		@Singleton
		public SessionContext sessionContext(@Named("host") String peer) {
			long stakeHolder = 1;
			Person stakeHolderValue = mock(Person.class);
			Host peerValue = mock(Host.class);
			return new SessionContextImpl(stakeHolder, stakeHolderValue, peer, peerValue, true);
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
		public CatalogEvaluationService evalService() {
			return mock(CatalogEvaluationService.class);
		}

		@Provides
		public CatalogDeserializationService catalogDeserializationService() {
			return mock(CatalogDeserializationService.class);
		}
	}

	CatalogDescriptor catalog;
	CatalogActionContext context;

	@Before
	public void setUp() throws Exception {
		ExcecutionContext excecution = injector.getInstance(ExcecutionContext.class);
		CatalogManager manager = injector.getInstance(CatalogManager.class);
		context = (CatalogActionContext) manager.spawn(excecution);
		catalog = builder.fromClass(Argument.class, "Argument", "Argument", 49723l);
		context.setCatalogDescriptor(catalog);
		context.setDomain(CatalogEntry.PUBLIC_ID);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}

	@Test
	public void crud() throws Exception {

		String TRES = "TRES";

		log.trace("[-create elements-]");

		List<Argument> argumentsToDeclare = Arrays.asList(new Argument("one", 1), new Argument("uno", 1),
				new Argument(TRES, 3), new Argument("four", 4), new Argument("five", 5));

		JDBCDataCreationCommand create = injector.getInstance(JDBCDataCreationCommand.class);
		for (Argument arg : argumentsToDeclare) {
			context.setEntryValue(arg);
			create.execute(context);
		}
		log.trace("[-read all-]");
		setUp();
		FilterData filterData = FilterDataUtils.newFilterData();
		context.setFilter(filterData);

		JDBCDataQueryCommand query = injector.getInstance(JDBCDataQueryCommand.class);
		query.execute(context);

		assertTrue(context.getResults().size() == argumentsToDeclare.size());

		log.trace("[-find element by single criteria-]");
		filterData = FilterDataUtils.createSingleFieldFilter(CatalogEntry.NAME_FIELD, TRES);
		context.setFilter(filterData);

		query.execute(context);
		List<CatalogEntry> results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		log.trace("[-read element-]");
		setUp();
		Object lodId = results.get(0).getId();
		context.setEntry(lodId);

		injector.getInstance(JDBCDataReadCommand.class).execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		log.trace("[update element]");
		context.setOldValue(context.getResult());
		context.setEntry(lodId);
		context.setEntryValue(new Argument("TROI", 3));

		injector.getInstance(JDBCDataWritingCommand.class).execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(lodId.equals(results.get(0).getId()));
		assertTrue(results.get(0).getName().equals("TROI"));

		log.trace("[delete element]");
		setUp();
		context.setEntry(lodId);

		injector.getInstance(JDBCDataDeleteCommand.class).execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(lodId.equals(results.get(0).getId()));

		setUp();

		filterData = FilterDataUtils.newFilterData();
		context.setFilter(filterData);
		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == (argumentsToDeclare.size() - 1));

		log.trace("[HSQL tests passed]");
		/*
		 * log.debug("testing comparators");
		 * 
		 * // EQUALS = "==";
		 * 
		 * criteria.setOperator(FilterData.EQUALS); criteria.setValue(1);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset();
		 * 
		 * // GREATEREQUALS = ">=";
		 * criteria.setOperator(FilterData.GREATEREQUALS);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 5);
		 * excecutionContext.reset();
		 * 
		 * // LESSEQUALS = "<=";
		 * 
		 * criteria.setOperator(FilterData.LESSEQUALS);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset();
		 * 
		 * // LESS = "<";
		 * 
		 * criteria.setOperator(FilterData.LESS); criteria.setValue(2);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset();
		 * 
		 * // GREATER = ">"; criteria.setOperator(FilterData.GREATER);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 3);
		 * excecutionContext.reset();
		 * 
		 * // DIFFEREN criteria.setOperator(FilterData.DIFFERENT);
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 5);
		 * excecutionContext.reset();
		 * 
		 * log.debug("testing string filters");
		 * 
		 * // STARTS criteria.setPath(Arrays.asList(CatalogEntry.NAME_FIELD));
		 * criteria.setOperator(FilterData.STARTS); criteria.setValue("f");
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset();
		 * 
		 * // ENDS = "END"; criteria.setOperator(FilterData.ENDS);
		 * criteria.setValue("o");
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 1);
		 * excecutionContext.reset();
		 * 
		 * // LIKE = "LIKE"; criteria.setOperator(FilterData.LIKE);
		 * criteria.setValue("f???");
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset();
		 * 
		 * criteria.setValue("f????");
		 * 
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 0);
		 * excecutionContext.reset();
		 * 
		 * criteria.setValue("f*");
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 2);
		 * excecutionContext.reset(); // REGEX = "REGEX";
		 * criteria.setOperator(FilterData.REGEX);
		 * 
		 * criteria.setValue("([A-Z])\\w+");
		 * muba.getContextProcessingCommand().execute(excecutionContext);
		 * catalogContext = excecutionContext.getServiceContext(); matches =
		 * catalogContext.getResults(); assertTrue(matches.size() == 1);
		 * excecutionContext.reset();
		 * 
		 * // TODO CONTAINS_EITHER = "HAS"; // TODO IN = "IN"; //
		 * FilterOperator.IN
		 */
	}

}
