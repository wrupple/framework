package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Booking;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.EventBus;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.junit.Test;

import java.util.Arrays;


/**
 * FIXME Test ExplicitOutputPlaceImpl
 */
public class ExplicitWorkflowInvocationTest extends BPMTest {



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
	/*
	 * DesktopEngineImpl DesktopBuilderContext DesktopRequestReader
	 * SearchEngineOptimizedDesktopWriterCommandImpl FormWriterImpl
	 * BrowserWriterImpl BusnessEvent ProcessControlNode
	 */



	@Test
	public void submitBookingData() throws Exception {

		String booking = Booking.CATALOG;
		String tracking = ApplicationState.CATALOG;
		ProcessTaskDescriptor assignBookingDriver;
		ProcessTaskDescriptor synthesizeTrackingObject;
		String rootActivity = "startTrip";
		String producedField = "trip";

		CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
		CatalogActionRequestImpl catalogRequest;
		CatalogActionContext ccontext;
		log.trace("[-create a reachable process in the application tree uniquely named startTrip -]");
		WorkflowImpl process = new WorkflowImpl();
		log.trace("[-register process as accepting Booking as input catalog and Tracking as output catalog-]");
		process.setCatalog(booking);
		//((ProcessDescriptorImpl) process).setCatalog(tracking);
        process.setProcessValues(
				Arrays.asList( assignBookingDriver, synthesizeTrackingObject));
		catalogRequest = new CatalogActionRequestImpl();
		catalogRequest.setEntryValue(process);
		catalogRequest.setName(CatalogActionRequest.CREATE_ACTION);
		catalogRequest.setCatalog(process.getCatalogType());
		runtimeContext.setServiceContract(catalogRequest);
		runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME);

		runtimeContext.process();

		ccontext = runtimeContext.getServiceContext();

		process = ccontext.getConvertedResult();

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
	// login is performed
		//
		// register bpm service manifest as SEO aware in the
	// tree//service manager ( dictionary like CatalogManager) to serve as
	//first order tree token processor
		//
		// the "who is concerned" part of
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


}
