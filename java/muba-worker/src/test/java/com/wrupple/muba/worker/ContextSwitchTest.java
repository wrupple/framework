package com.wrupple.muba.worker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.CatalogEventListenerImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.domain.impl.WorkerRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
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

        // expectations
        replayAll();

        CatalogDescriptorBuilder builder = container.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-register catalogs-]");



        CatalogDescriptor managed = builder.fromClass(ManagedObjectImpl.class, ManagedObjectImpl.class.getSimpleName(),
                ManagedObjectImpl.class.getSimpleName(), 2, container.getInjector().getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));

        FieldDescriptor stakeHolderField = managed.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
        assertTrue ("stakeHolder field missing",stakeHolderField != null);
        assertTrue ("stakeHolder is multiple",!stakeHolderField.isMultiple());
        assertTrue ("stakeHolder has the wrong data type",stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE);
        assertTrue ("stakeHolder is not a Person key",Person.CATALOG.equals(stakeHolderField.getCatalog()));


        CatalogActionRequestImpl action = new CatalogCreateRequestImpl(managed,CatalogDescriptor.CATALOG_ID);

        managed = (CatalogDescriptor) ((List)container.fireEvent(action)).get(0);

        CatalogDescriptor bookingDescriptor = builder.fromClass(RiderBooking.class, RiderBooking.class.getSimpleName(),
                RiderBooking.class.getSimpleName(), 0, managed);

         stakeHolderField = bookingDescriptor.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
        assertTrue ("Booking inherit ManagedObject",stakeHolderField != null && !stakeHolderField.isMultiple()
                && stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
                && Person.CATALOG.equals(stakeHolderField.getCatalog()));

        bookingDescriptor.setConsolidated(true);

         action = new CatalogCreateRequestImpl(bookingDescriptor,CatalogDescriptor.CATALOG_ID);

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


        log.trace("[-create application tree-]");
        ApplicationImpl root = new ApplicationImpl();
        root.setDistinguishedName(HOME);

        ApplicationImpl item = new ApplicationImpl();
        String testActivity = "createTrip";
        item.setDistinguishedName(testActivity);
        item.setProcessValues(Arrays.asList(pickDriver,updateBooking));
        //this tells bpm to use this application to resolve bookings
        //item.setCatalog(bookingDescriptor.getDistinguishedName());
        //item.setOutputCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputField("riderBooking");

        root.setChildrenValues(Arrays.<ServiceManifest>asList(item));
        action = new CatalogCreateRequestImpl(root,Application.CATALOG);
        action.setFollowReferences(true);

        container.fireEvent(action);

        root = (ApplicationImpl) action.getEntryValue();

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
//        assertTrue(riderBooking.getStakeHolder()!=null);
//        assertTrue(riderBooking.getTimestamp()!=null);

        log.trace("[-use riderBooking id to launch container with previously created riderBooking -]");
        container.fireEvent(new WorkerRequestImpl(
                Arrays.asList(testActivity, riderBooking.getId().toString()),
                container.getInjector().getInstance(Key.get(Long.class,Names.named("com.wrupple.runner.choco"))),
                HOME
        ));
        //check conditions
        assertTrue(riderBooking.getDriverValue()!=null);
        //assertTrue(Math.abs(riderBooking.getDriverValue().getLocation()-riderBooking.getLocation())<0);

    }

}
