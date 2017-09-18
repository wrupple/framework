package com.wrupple.muba.catalogs.server.chain.command;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.event.server.service.FormatDictionary;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.MainModule;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.domain.SessionContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCHSQLTestModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.Argument;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.event.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataCreationCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataDeleteCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataQueryCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataWritingCommandImpl;
import com.wrupple.muba.catalogs.server.domain.FilterCriteriaImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.impl.UserCatalogPluginImpl;

public class HSQLTest extends MubaTest {


	public HSQLTest() {
		init(new CRUDModule(), new CatalogModule(), new SingleUserModule(), new JDBCHSQLTestModule(),
				new HSQLDBModule(), new JDBCModule(), new ValidationModule(), new MainModule());

	}
	

	@Override
	protected void registerServices(Validator v, ValidationGroupProvider g, EventBus switchs) {
		CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
		switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));
	}

	/*
	 * mocks
	 */

	private class CRUDModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

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
			bind(UserCatalogPlugin.class).to(UserCatalogPluginImpl.class);
			bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
			bind(CatalogFileUploadUrlHandlerTransaction.class)
					.toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));

			WriteOutput mockWriter = mock(WriteOutput.class);
			WriteAuditTrails mockLogger = mock(WriteAuditTrails.class);
			EventSuscriptionChain mockSuscriptorChain = mock(EventSuscriptionChain.class);

			bind(EventSuscriptionChain.class).toInstance(mockSuscriptorChain);
			bind(WriteAuditTrails.class).toInstance(mockLogger);
			bind(WriteOutput.class).toInstance(mockWriter);

            bind(FormatDictionary.class).toInstance(mock(FormatDictionary.class));

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

	CatalogDescriptor catalog;
	CatalogActionContext context;

	@Before
	public void setUp() throws Exception {
		RuntimeContext excecution = injector.getInstance(RuntimeContext.class);
		SystemCatalogPlugin manager = injector.getInstance(SystemCatalogPlugin.class);
		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		context = manager.spawn(excecution);
		catalog = builder.fromClass(Argument.class, "Argument", "Argument", -49723l, null);
		context.setCatalogDescriptor(catalog);
		context.setDomain(CatalogEntry.PUBLIC_ID);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}

	@Test
	public void crud() throws Exception {

		String TRES = "TRES";
		String FIVE = "five";

		log.trace("[-create elements-]");

		List<Argument> argumentsToDeclare = Arrays.asList(new Argument(TRES, 3l), new Argument(FIVE, 5l),
				new Argument("one", 1l), new Argument("uno", 1l), new Argument("four", 4l));

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
		List<CatalogEntry> results = context.getResults();

		log.trace("[-read element-]");
		setUp();
		Object lodId = results.get(0).getId();
		context.setEntry(lodId);

		injector.getInstance(JDBCDataReadCommand.class).execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		log.trace("[-read all in order-]");
		// ORDER
		context.setEntry(null);
		context.setFilter(filterData);
		filterData.addOrdering(new FilterDataOrderingImpl(Argument.VALUE, false));

		query.execute(context);
		assertTrue(context.getResults().size() == argumentsToDeclare.size());
		assertTrue(((Argument) context.getResults().get(0)).getValue() == 5);

		log.trace("[-read a segment-]");
		// LIMITS log.trace("[-read a segment-]");
		filterData.setConstrained(true);
		filterData.setStart(2);
		filterData.setLength(2);
		query.execute(context);
		assertTrue(context.getResults().size() == 2);

		// EQUALS = "==";
		log.trace("[-find element by single == criteria-]");
		filterData = FilterDataUtils.createSingleFieldFilter(CatalogEntry.NAME_FIELD, TRES);
		context.setFilter(filterData);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getName().equals(TRES));

		filterData = FilterDataUtils.newFilterData();
		FilterCriteriaImpl criteria = new FilterCriteriaImpl(Argument.VALUE, FilterData.DIFFERENT, 1l);
		filterData.addFilter(criteria);
		context.setFilter(filterData);

		// DIFFERENT
		log.trace("[-find element by single != criteria-]");
		criteria.setOperator(FilterData.DIFFERENT);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 3);

		// GREATEREQUALS = ">=";
		log.trace("[-find element by single >= criteria-]");
		criteria.setOperator(FilterData.GREATEREQUALS);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 5);

		// LESSEQUALS = "<=";
		log.trace("[-find element by single <= criteria-]");
		criteria.setOperator(FilterData.LESSEQUALS);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 2);

		// LESS = "<"
		log.trace("[-find element by single < criteria-]");
		criteria.setOperator(FilterData.LESS);
		criteria.setValue(2l);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 2);

		// GREATER = ">"
		log.trace("[-find element by single > criteria-]");
		criteria.setOperator(FilterData.GREATER);

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 3);

		log.debug("testing string filters");
		// LIKE
		log.trace("[-find element by single LIKE criteria-]");
		criteria.setPath(Arrays.asList(CatalogEntry.NAME_FIELD));
		criteria.setOperator(FilterData.LIKE);
		/*
		criteria.setValue("f???");

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 1);

		criteria.setValue("f????");

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 0);*/

		criteria.setValue("f%");

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 2);

		/*
		 * 
		 * 
		 * STARTS log.trace("[-find element by single STARTS criteria-]");
		 * 
		 * criteria.setOperator(FilterData.STARTS); criteria.setValue("f");
		 * 
		 * query.execute(context); results = context.getResults();
		 * assertTrue(results.size() == 2);
		 */

		/*
		 * ENDS log.trace("[-find element by single ENDS criteria-]");
		 * criteria.setOperator(FilterData.ENDS); criteria.setValue("o");
		 * 
		 * query.execute(context); results = context.getResults();
		 * assertTrue(results.size() == 1);
		 */

		/*
		 * log.trace("[-find element by single REGEX criteria-]");
		 * criteria.setOperator(FilterData.REGEX);
		 * criteria.setValue("([A-Z])\\w+");
		 * 
		 * query.execute(context); results = context.getResults();
		 * assertTrue(results.size() == 1);
		 */
		
		log.debug("testing collection filters");
		// IN
		log.trace("[-find element by single IN criteria-]");
		criteria.setOperator(FilterData.IN);
		criteria.setValues(Arrays.asList((Object)TRES,FIVE));

		query.execute(context);
		results = context.getResults();
		assertTrue(results.size() == 2);

		/*
		 * FINISHED QUERY TESTS
		 */
		log.trace("[update element]");
		context.setOldValue((CatalogEntry) context.getConvertedResult());
		context.setEntry(lodId);
		context.setEntryValue(new Argument("TROI", 3l));

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

		log.trace("[CRUD tests passed]");

	}


	// i18n locale-dependent field values

}
