package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bpm.domain.Booking;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.event.EventBus;

import com.wrupple.muba.MubaTest;
import org.junit.Test;


/**
 * FIXME Test ExplicitOutputPlaceImpl
 */
public class ExplicitWorkflowInvocationTest extends MubaTest {
	@Override
	protected void registerServices(EventBus switchs) {

	}

	@Override
	protected void setUp() throws Exception {

		//FIXME this tests catching ApplicationUpdateEvent And firing a workflow (History change event?)
		/**
         * Done -Triggers are serializable listeners, move trigger logit to an event (fire a workflow with one catalog task)
		 * Done - fire a catalog change event (PublishEventsImpl) ( install remote listeners on Event CHain (vegetate web hooks))
         * ???? - listen event  and have the desktop fire the first task of a workflow and install a listener for the task submission(desktop)
		 **/
		//CREATE ApplicationState
		//INSTALL LISTENER HERE
		//Update ApplicationState
        //Expect (but don't install an instance of) ApplicationStateUpdatePlace to be fired
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
	 *
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

		 // save to field Business event as something that changes the state of a
		 // process or task within a process, always return the tracking id/value

		  //submit, select (including select an action), modify model, modify
		  //Widget?
		 //
		log.trace("[-finish process by submitting catalog Entry context-]");

		log.trace("[-handle user output (create notification)-]");
		// Catalogs:
		// taxi,Persona(Pasajero),TripBooking,PaymentSource(PendingAproval)
		// muba.exec("startTrip/",{Booking});
		//TODO trying to create a booking constrain violates empty payment source and switches to payment source selection process
	//TODO Creation of a tracking object requires stakeHolder and so a
	// process switch is attempted and failed and then a context muba.switch to
	// login is performed register bpm service manifest as SEO aware in the
	// tree//service manager ( dictionary like CatalogManager) to serve as
	//first order tree token processor the "who is concerned" part of
	//PublishEventsImpl should really be BPM's problem
	//
	//
		runtimeContext.reset();
		BusinessIntent submit;
		runtimeContext.setServiceContract(submit);
		runtimeContext.setSentence(rootActivity, synthesizeTrackingObject);
		HumanActivityTracking tracking = runtimeContext.getServiceContext();

		assertTrue(tracking.getPropertyvalue(task.getProducedField()) != null);

		log.trace("[-outputHandler creates notificationt-]");

		// expectations

		replayAll();
	//
	//read task defined tokens
	//
		// is own parent or duplicated fields
		CatalogDescriptor problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
				"Math Problem", 0, builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG,
						ContentNode.class.getSimpleName(), -1l, null));

		CatalogActionRequestImpl action = new CatalogActionRequestImpl();
		action.setEntryValue(problemContract);

		runtimeContext.setServiceContract(action);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
				CatalogActiprivate JsNotification output;
	private StateTransition<DesktopPlace> callback;
	private String[] activity;
	private StorageManager desc;
	private StorageManager sm;
	private boolean entry;
	private JavaScriptObject properties;
	private ProcessContextServices processContext;onRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

		runtimeContext.process();

		runtimeContext.reset();
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
/*
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
*/
}
