package com.wrupple.muba.worker;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.domain.impl.WorkerRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.RiderBooking;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ContextSwitchTest extends WorkerTest {



    @Test
    public void submitBookingData() throws Exception {

        CatalogDescriptorBuilder builder = container.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-register catalogs-]");

        // expectations

        replayAll();

        CatalogDescriptor bookingDescriptor = builder.fromClass(RiderBooking.class, RiderBooking.class.getSimpleName(),
                RiderBooking.class.getSimpleName(), 0, null);

        CatalogActionRequestImpl action = new CatalogCreateRequestImpl(bookingDescriptor,CatalogDescriptor.CATALOG_ID);

        container.fireEvent(action);

        action = new CatalogCreateRequestImpl(builder.fromClass(Driver.class, Driver.class.getSimpleName(),
                "Driver", 1, null),CatalogDescriptor.CATALOG_ID);

        container.fireEvent(action);

        log.trace("[-create tasks (problem definition)-]");

        Task pickDriver = new TaskImpl();
        pickDriver.setDistinguishedName("driverPick");
        pickDriver.setName("Pick Best Driver");
        pickDriver.setCatalog(Driver.class.getSimpleName());
        pickDriver.setName(Task.SELECT_COMMAND);
       /* problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        Task.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );*/


        Task updateBooking = new TaskImpl();
        updateBooking.setDistinguishedName("UpdateBooking");
        updateBooking.setName("Update DriverBooking");
        updateBooking.setCatalog(RiderBooking.class.getSimpleName());
        updateBooking.setName(CatalogActionRequest.WRITE_ACTION);


        log.trace("[-create riderBooking data handling application item-]");
        ApplicationImpl item = new ApplicationImpl();

        item.setDistinguishedName("createTrip");;
        item.setProcessValues(Arrays.asList(pickDriver,updateBooking));
        //this tells bpm to use this application to resolve bookings
        //item.setCatalog(bookingDescriptor.getDistinguishedName());
        //item.setOutputCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputField("riderBooking");

        action = new CatalogCreateRequestImpl(item,Application.CATALOG);
        action.setFollowReferences(true);

        container.fireEvent(action);

        item = (ApplicationImpl) action.getEntryValue();

        log.trace("[-create a pool of drivers to resolve the riderBooking-]");

        super.createMockDrivers();

        log.trace("[-Create a DriverBooking-]");

        RiderBooking riderBooking = new RiderBooking();
        riderBooking.setLocation(7l);
        riderBooking.setName("test");

        action = new CatalogCreateRequestImpl(riderBooking,RiderBooking.class.getSimpleName());
        action.setFollowReferences(true);

        List<RiderBooking> results = container.fireEvent(action);

        riderBooking = (RiderBooking) results.get(0);

        assertTrue(riderBooking.getId()!=null);
        assertTrue(riderBooking.getStakeHolder()!=null);

        log.trace("[-use riderBooking id to launch container with previously created riderBooking -]");
        container.fireEvent(new WorkerRequestImpl(Arrays.asList(item.getDistinguishedName(), riderBooking.getId().toString())));
        //check conditions
        assertTrue(riderBooking.getDriverValue()!=null);
        //assertTrue(Math.abs(riderBooking.getDriverValue().getLocation()-riderBooking.getLocation())<0);

    }

}
