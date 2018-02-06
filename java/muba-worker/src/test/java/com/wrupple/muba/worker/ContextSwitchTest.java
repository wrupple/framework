package com.wrupple.muba.worker;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.domain.impl.ContainerRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.Booking;
import com.wrupple.muba.worker.domain.BusinessServiceManifest;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.impl.BusinessIntentImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.domain.impl.WorkflowImpl;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.anyObject;

public class ContextSwitchTest extends WorkerTest {



    @Test
    public void submitBookingData() throws Exception {

        CatalogDescriptorBuilder builder = container.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-register catalogs-]");

        // expectations

        replayAll();

        CatalogDescriptor bookingDescriptor = builder.fromClass(Booking.class, Booking.class.getSimpleName(),
                "Booking", 0, null);

        CatalogActionRequestImpl action = new CatalogActionRequestImpl();
        action.setEntryValue(bookingDescriptor);
        action.setFollowReferences(true);

        container.fireEvent(action);
        action = new CatalogActionRequestImpl();

        action.setEntryValue(builder.fromClass(Driver.class, Driver.class.getSimpleName(),
                "Driver", 1, null));
        action.setFollowReferences(true);

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
        updateBooking.setName("Update Booking");
        updateBooking.setCatalog(Booking.class.getSimpleName());
        updateBooking.setName(CatalogActionRequest.WRITE_ACTION);


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

        container.fireEvent(action);

        item = (WorkflowImpl) action.getEntryValue();

        log.trace("[-create a pool of drivers to resolve the booking-]");

        super.createMockDrivers();

        log.trace("[-Create a Booking-]");

        Booking booking = new Booking();
        booking.setLocation(7);
        booking.setName("test");

        action = new CatalogCreateRequestImpl(booking,Booking.class.getSimpleName());
        action.setFollowReferences(true);
        booking = (Booking) action.getEntryValue();

        container.fireEvent(action);

        log.trace("[-use booking id to launch container with prviously created booking -]");
        assertTrue(booking.getId()!=null);
        assertTrue(booking.getStakeHolder()!=null);
        container.fireEvent(new ContainerRequestImpl(Arrays.asList(item.getDistinguishedName(),booking.getId().toString())));
        //check conditions
        assertTrue(booking.getDriverValue()!=null);
        //assertTrue(Math.abs(booking.getDriverValue().getLocation()-booking.getLocation())<1);

    }

}
