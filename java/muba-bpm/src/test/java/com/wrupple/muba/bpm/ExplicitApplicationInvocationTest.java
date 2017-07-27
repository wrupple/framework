package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.MockRunnerModule;
import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
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
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;


public class ExplicitApplicationInvocationTest extends MubaTest {
	/*
	 * mocks
	 */

	protected WriteOutput mockWriter;

	protected WriteAuditTrails mockLogger;

	protected CatalogPeer peerValue;

	protected EventSuscriptionChain chainMock;

	class PrivateModule extends AbstractModule {

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

	public ExplicitApplicationInvocationTest() {
		init(new PrivateModule(), new BPMTestModule(), new SingleUserModule(),new ChocoSolverModule(),new SolverModule(),new HSQLDBModule(), new JDBCModule(),
				new ValidationModule(), new CatalogModule(), new BootstrapModule());
	}

	@Override
	protected void registerServices(Validator v, ValidationGroupProvider g, SystemContext switchs) {
		CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
		switchs.registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class));
		switchs.registerContractInterpret(catalogServiceManifest, injector.getInstance(CatalogRequestInterpret.class));

		SolverServiceManifest solverServiceManifest = injector.getInstance(SolverServiceManifest.class);
		switchs.registerService(solverServiceManifest, injector.getInstance(SolverEngine.class));
		switchs.registerContractInterpret(solverServiceManifest, injector.getInstance(ActivityRequestInterpret.class));
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
		runtimeContext.setServiceContract(catalogRequest);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME);

		runtimeContext.process();

		ccontext = runtimeContext.getServiceContext();

		process = ccontext.getResult();

		runtimeContext.reset();

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
		runtimeContext.reset();
		BusinessEvent submit;
		runtimeContext.setServiceContract(submit);
		runtimeContext.setSentence(rootActivity, synthesizeTrackingObject);
		HumanActivityTracking tracking = runtimeContext.getServiceContext();

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

		runtimeContext.setServiceContract(action);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

		runtimeContext.process();

		runtimeContext.reset();
	}*/

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
