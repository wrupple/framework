package com.wrupple.muba.bpm;


import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bpm.domain.impl.BusinessIntentImpl;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.command.WorkflowEventInterpret;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.IntentResolverEngine;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.IntentResolverRequestInterpret;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.server.service.EventRegistry;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;

import java.util.Arrays;


public class ImplicitCatalogDiscriminationApplicationTest  extends BPMTest {


    private RuntimeContext runtimeContext;

    @Before
    public void setUp() throws Exception {

         runtimeContext = injector.getInstance(RuntimeContext.class);
        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-register catalogs-]");

        // expectations

        replayAll();

        CatalogDescriptor bookingDescriptor = builder.fromClass(Booking.class, Booking.class.getSimpleName(),
                "Booking", 0, null);

        CatalogActionRequestImpl action = new CatalogActionRequestImpl();
        action.setEntryValue(bookingDescriptor);
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.process();
        runtimeContext.reset();

        action.setEntryValue(builder.fromClass(Driver.class, Driver.class.getSimpleName(),
                "Driver", 1, null));
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.process();

        runtimeContext.reset();

        log.trace("[-create tasks (problem definition)-]");

        ProcessTaskDescriptor pickDriver = new ProcessTaskDescriptorImpl();
        pickDriver.setDistinguishedName("driverPick");
        pickDriver.setName("Pick Best Driver");
        pickDriver.setCatalog(Driver.class.getSimpleName());
        pickDriver.setTransactionType(ProcessTaskDescriptor.SELECT_COMMAND);
       /* problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        ProcessTaskDescriptor.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        ProcessTaskDescriptor.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );*/


        ProcessTaskDescriptor updateBooking = new ProcessTaskDescriptorImpl();
        updateBooking.setDistinguishedName("UpdateBooking");
        updateBooking.setName("Update Booking");
        updateBooking.setCatalog(Booking.class.getSimpleName());
        updateBooking.setTransactionType(CatalogActionRequest.WRITE_ACTION);


        log.trace("[-create booking data handling application item-]");
        WorkflowImpl item = new WorkflowImpl();

        item.setDistinguishedName("createTrip");;
        item.setProcessValues(Arrays.asList(pickDriver,updateBooking));
        //this tells bpm to use this application to resolve bookings
        item.setCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputField("booking");

        action = new CatalogActionRequestImpl();
        action.setFollowReferences(true);
        action.setEntryValue(item);

        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, Workflow.CATALOG, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        item = catalogContext.getEntryResult();

        runtimeContext.reset();




        log.trace("[-create a pool of drivers to resolve the booking-]");

        Driver driver;
        for(int i = 0 ; i < 10 ; i++){
            driver = new Driver();
            //thus, best driver will have a location of 6, or 8 because 7 will not be available
            driver.setLocation(i);
            driver.setAvailable(i%2==0);

            action.setEntryValue(driver);

            runtimeContext.setServiceContract(action);
            runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                    CatalogActionRequest.LOCALE_FIELD, Booking.class.getSimpleName(), CatalogActionRequest.CREATE_ACTION);

            runtimeContext.process();

            runtimeContext.reset();
        }

        log.trace("[-Create a Booking-]");

        booking = new Booking();
        booking.setLocation(7);
        booking.setName("test");

        action = new CatalogActionRequestImpl();
        action.setFollowReferences(true);
        action.setEntryValue(booking);

        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, Booking.class.getSimpleName(), CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();
        booking = catalogContext.getEntryResult();

        runtimeContext.reset();
        
      


        runtimeContext = injector.getInstance(RuntimeContext.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }

    Booking booking;

    @Test
    public void submitBookingData() throws Exception {
        //FIXME rewrite this with events
        log.trace("[-Ask BPM what application item to use to handle this booking-]");

        runtimeContext.setSentence(IntentResolverServiceManifest.SERVICE_NAME,Booking.class.getSimpleName(),Booking.class.getSimpleName());

        runtimeContext.process();

        //THE RESULT OF PROCESING AN IMPLICIT INTENT IS AN EXPLICIT INTENT
        WorkflowImpl item = runtimeContext.getConvertedResult();

        runtimeContext.reset();

        log.trace("[-Create Booking Handling Application Context-]");

        //item+booking;
        BusinessIntentImpl bookingRequest = new BusinessIntentImpl();
        bookingRequest.setHandle(item.getId());
        bookingRequest.setEntry(booking.getId());
        bookingRequest.setStateValue(null /*this means create a new activity context, otherwise the context would be retrived*/);


        //BOOKING IS SAVED AS entry value (result) on the initial application state
        runtimeContext.setServiceContract(bookingRequest);
        //runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME);

        //FIXME maybe this is the point we start using event handlers
        //runtimeContext.setServiceContract(activityState);
        runtimeContext.setSentence(BusinessServiceManifest.SERVICE_NAME);

        runtimeContext.process();
        //a new activity state
        ApplicationState activityState = runtimeContext.getConvertedResult();

        String applicationId = activityState.getDistinguishedName();

        runtimeContext.reset();

        Long firstTask = activityState.getTaskDescriptor();

        assertTrue("First task has been assigned",firstTask!=null);


        log.info("find solution of first task to the runner engine");
        //the best available driver

        runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME,applicationId);

        runtimeContext.process();

        Driver driver = runtimeContext.getConvertedResult();

        runtimeContext.reset();

        log.info("post solution of first task to the business engine");

        bookingRequest = new BusinessIntentImpl();
        bookingRequest.setEntryValue(driver);
        //we explicitly avoid exposing the applicationId to test service location bookingRequest.setStateValue((Long) activityState.getId());

        //BOOKING IS SAVED AS entry value (result) on the initial application state
        runtimeContext.setServiceContract(bookingRequest);
        runtimeContext.setSentence(BusinessServiceManifest.SERVICE_NAME,applicationId);

        runtimeContext.process();

        activityState = runtimeContext.getConvertedResult();

        assertTrue("Follow task has been assigned",activityState.getTaskDescriptor()!=null&&!firstTask.equals(activityState.getTaskDescriptor()));

        runtimeContext.reset();

        log.info("manually solving the second task");
        //set solution of second task in activity state
        booking.setDriverValue(driver);
        activityState.setEntryValue(booking);

        log.info("post solution of second task to the business engine");

        runtimeContext.setServiceContract(activityState);
        runtimeContext.setSentence(BusinessServiceManifest.SERVICE_NAME,activityState.getDistinguishedName());

        runtimeContext.process();

        activityState = runtimeContext.getConvertedResult();
        runtimeContext.reset();

        booking = (Booking) activityState.getEntryValue();
        assertTrue(booking.getStakeHolder()!=null);
        assertTrue(booking.getDriverValue()!=null);
        assertTrue(Math.abs(booking.getDriverValue().getLocation()-booking.getLocation())==1);
    }

}