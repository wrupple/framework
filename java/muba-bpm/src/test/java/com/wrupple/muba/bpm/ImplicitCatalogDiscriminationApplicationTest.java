package com.wrupple.muba.bpm;


import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.ApplicationItemImpl;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;

import java.util.Arrays;


public class ImplicitCatalogDiscriminationApplicationTest  extends MubaTest {


    public ImplicitCatalogDiscriminationApplicationTest() {
        init( new BPMTestModule(), new SingleUserModule(), new SolverModule(), new HSQLDBModule(), new JDBCModule(),
                new ValidationModule(), new CatalogModule(), new BootstrapModule());
    }

    @Override
    protected void registerServices(SystemContext switchs) {
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

    @Test
    public void submitBookingData() throws Exception {

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
        ApplicationItemImpl item = new ApplicationItemImpl();

        item.setDistinguishedName("createTrip");;
        item.setProcessValue(Arrays.asList(pickDriver,updateBooking));
        //this tells bpm to use this application to resolve bookings
        item.setCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputCatalog(bookingDescriptor.getDistinguishedName());

        action = new CatalogActionRequestImpl();
        action.setFollowReferences(true);
        action.setEntryValue(item);

        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, ApplicationItem.CATALOG, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        item = catalogContext.getEntryResult();

        runtimeContext.reset();

        log.trace("[-Ask BPM what application item to use to resolve a booking-]");

        TODO

    }

}