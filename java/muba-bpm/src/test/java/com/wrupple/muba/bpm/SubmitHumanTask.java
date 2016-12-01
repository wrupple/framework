package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.ParentServiceManifest;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.server.chain.command.ValidateContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.HumanActivityTracking;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.impl.ProcessDescriptorImpl;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCHSQLTestModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
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

public class SubmitHumanTask extends MubaTest {

	private static final String DOMAIN_A = "1", DOMAIN_B = "2";
	/*
	 * mocks
	 */

	protected ValidateContext mockValidator;

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	class BPMTestModule extends AbstractModule {

		@Override
		protected void configure() {

			// this makes JDBC the default storage unit
			bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
			bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
			bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
			bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
			bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

			// mocks
			mockValidator = mock(ValidateContext.class);
			mockWriter = mock(WriteOutput.class);
			mockLogger = mock(WriteAuditTrails.class);
			bind(WriteAuditTrails.class).toInstance(mockLogger);
			bind(WriteOutput.class).toInstance(mockWriter);
			bind(ValidateContext.class).toInstance(mockValidator);

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
		public CatalogDeserializationService catalogDeserializationService() {
			return mock(CatalogDeserializationService.class);
		}

	}

	public SubmitHumanTask() {
		init(new BPMTestModule(), new JDBCModule(),
				new JDBCHSQLTestModule(/* // this is what makes it purr */), new HSQLDBModule(), new SingleUserModule(),
				new CatalogModule(), new BootstrapModule());
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		expect(mockValidator.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
		excecutionContext = injector.getInstance(ExcecutionContext.class);
		log.trace("NEW TEST EXCECUTION CONTEXT READY");
	}
	/*
	 * DesktopEngineImpl DesktopBuilderContext DesktopRequestReader
	 * SearchEngineOptimizedDesktopWriterCommandImpl FormWriterImpl
	 * BrowserWriterImpl BusnessEvent ProcessControlNode
	 */

	/**
	 * 
	 * 
	 * //comprar el taxi mas cercano, libre con mejor rating //input:
	 * Booking-quote-Invoice-HasListing{ paymentMethod, location } //output:
	 * tracking
	 * 
	 * Test the tracking state changes in accordance to the task excecuted
	 * (task.getProducedField())
	 * 
	 * @throws Exception
	 */
	@Test
	public void submitBookingData() throws Exception {

		String booking = Booking.CAGALOG;
		String tracking = Tracking.CATALOG;
		ProcessTaskDescriptor assignBookingDriver;
		ProcessTaskDescriptor synthesizeTrackingObject;
		String rootActivity = "startTrip";
		String task.getProducedField() = "trip";

		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		CatalogActionRequestImpl catalogRequest;
		CatalogActionContext ccontext;
		log.trace("[-create a reachable process in the application tree uniquely named startTrip -]");
		ProcessDescriptor process = new ProcessDescriptorImpl();
		log.trace("[-register process as accepting Booking as input catalog and Tracking as output catalog-]");
		((ProcessDescriptorImpl) process).setCatalog(booking);
		((ProcessDescriptorImpl) process).setCatalog(tracking);
		((ProcessDescriptorImpl) process).setProcessStepsValues(
				Arrays.asList( assignBookingDriver, synthesizeTrackingObject));
		catalogRequest = new CatalogActionRequestImpl();
		catalogRequest.setEntryValue(process);
		catalogRequest.setAction(CatalogActionRequest.CREATE_ACTION);
		catalogRequest.setCatalog(process.getCatalogType());
		excecutionContext.setServiceContract(catalogRequest);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME);

		excecutionContext.process();

		ccontext = excecutionContext.getServiceContext();

		process = ccontext.getResult();

		excecutionContext.reset();

		log.trace("[-create process excecution context (tracking) by submitting multiple selection-]");
		/*
		 * save to field Business event as something that changes the state of a
		 * process or task within a process, always return the tracking id/value
		 * 
		 * submit, select (including select an action), modify model, modify
		 * Widget?
		 */
		log.trace("[-finish process by submitting catalog Entry context-]");

		log.trace("[-handle user output (create notification)-]");
		// Catalogs:
		// taxi,Persona(Pasajero),TripBooking,PaymentSource(PendingAproval)
		// muba.exec("startTrip/",{Booking});
		/*//TODO trying to create a booking constrain violates empty payment source and switches to payment source selection process
		 * TODO Creation of a tracking object requires stakeHolder and so a
		 * process switch is attempted and failed and then a context muba.switch to
		 * login is performed register bpm service manifest as SEO aware in the
		 * tree//service manager ( dictionary like CatalogManager) to serve as
		 * first order tree token processor the "who is concerned" part of
		 * PublishEventsImpl should really be BPM's problem
		 * 
		 */
		excecutionContext.reset();
		BusinessEvent submit;
		excecutionContext.setServiceContract(submit);
		excecutionContext.setSentence(rootActivity, synthesizeTrackingObject);
		HumanActivityTracking tracking = excecutionContext.getServiceContext();

		assertTrue(tracking.getPropertyvalue(task.getProducedField()) != null);

		log.trace("[-outputHandler creates notificationt-]");

		// expectations

		replayAll();
		/*
		 * read task defined tokens
		 */
		// is own parent or duplicated fields
		CatalogDescriptor problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
				"Math Problem", 0, builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG,
						ContentNode.class.getSimpleName(), -1l, null));

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(problemContract);

		excecutionContext.setServiceContract(action);
		excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

		excecutionContext.process();

		excecutionContext.reset();
	}

	CatalogDataAccessObject<ProcessTaskDescriptor> taskDao = context.getDataStoreManager().getOrAssembleDataSource(
			ProcessTaskDescriptor.CATALOG, context.getCatalogContext(), ProcessTaskDescriptor.class);
	ProcessTaskDescriptor submittingTask = taskDao.read(submittingTaskId);
	String transactionType = submittingTask.getTransactionType();
	/*
	 * all forms that use a context.getSubmitUrl() should append a hidden field
	 * with the id of the submitting task. if this field is present we know we
	 * have a form submission in our hands
	 * 
	 * in that case the use output context variable is constituted by the
	 * resulting entrie(s) of committing the submitting task
	 * 
	 * see how to handle different submissions cause redirecting is a
	 * posibility, se how submitUrl is determined to also know how to handle
	 * sumissions
	 *
	 * 
	 * also userOutputVariable should be used, since client uses that variable
	 * to deduce what item to show in read transactions El cliente debería de
	 * poder determinar el estado en el que se encuentra una petición únicamente
	 * por la URL
	 * 
	 * reader should check for submitts before wasting any more resources and
	 * perform redirects filling out apropiate url tokens (at least entry and
	 * ?catalog? according to task configuration if necesary
	 */

	if(CatalogActionRequest.READ_ACTION.equals(transactionType))
	{
		// assume a selection is submitted
		String[] selectedIds = context.getRequest().getParameterValues(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);

		// TODO what to do now that i've selected? just save them to user output

	}else if(CatalogActionRequest.WRITE_ACTION.equals(transactionType))
	{

		String updatedId = context.getRequest().getParameter(CatalogEntry.ID_FIELD);

		// TODO write y guardarlo en user output

	}else if(CatalogActionRequest.CREATE_ACTION.equals(transactionType))
	{

		// TODO create and save to user output

	}else
	{
		throw new IllegalArgumentException("unsupported ");
	}

}
