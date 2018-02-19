package com.wrupple.muba.worker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.desktop.domain.impl.WorkerContractImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.worker.domain.RiderBooking;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.shared.services.WorkerContainer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ContextSwitchTest extends WorkerTest {

    static final String HOME = "resolveBooking";

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

        managed = container.fireEvent(action);

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

        log.trace("[-create application tree-]");
        ApplicationImpl root = createApplication(container,HOME);

        assertTrue("Application tree not created",!root.getChildrenValues().get(0).getChildrenValues().isEmpty());

        log.trace("[-create a pool of drivers to resolve the riderBooking-]");

        super.createMockDrivers();

        log.trace("[-Create a DriverBooking-]");

        RiderBooking riderBooking = new RiderBooking();
        riderBooking.setLocation(7l);
        riderBooking.setName("test");

        action = new CatalogCreateRequestImpl(riderBooking,RiderBooking.class.getSimpleName());
        action.setFollowReferences(true);


        riderBooking =  container.fireEvent(action);

        assertTrue(riderBooking.getId()!=null);
//        assertTrue(riderBooking.getStakeHolder()!=null);
//        assertTrue(riderBooking.getTimestamp()!=null);

        log.trace("[-use riderBooking id to launch container with previously created riderBooking -]");
        container.fireEvent(new WorkerContractImpl(
                Arrays.asList(riderBooking.getId().toString()),
                container.getInjector().getInstance(Key.get(Long.class,Names.named("com.wrupple.runner.choco"))),
                HOME
        ));
        //check conditions
        assertTrue(riderBooking.getDriverValue()!=null);
        //assertTrue(Math.abs(riderBooking.getDriverValue().getLocation()-riderBooking.getLocation())<0);

    }

    private ApplicationImpl createApplication(WorkerContainer container, String home) throws Exception {

        TaskImpl resolve  = new TaskImpl();
        resolve.setDistinguishedName("findDriver");
        resolve.setName(DataContract.WRITE_ACTION);
        resolve.setCatalog(RiderBooking.class.getSimpleName());

        TaskImpl cargar  = new TaskImpl();
        cargar.setDistinguishedName("loadBooking");
        cargar.setName(DataContract.READ_ACTION);
        cargar.setCatalog(RiderBooking.class.getSimpleName());

        ApplicationImpl ilegal= new ApplicationImpl();
        ilegal.setDistinguishedName("peticionInvalida");

        ApplicationImpl trabajo = new ApplicationImpl();
        trabajo.setDistinguishedName("findDriver");
        trabajo.setProcessValues(Arrays.asList(resolve));

        ApplicationImpl terminado = new ApplicationImpl();
        terminado.setDistinguishedName("terminado");

        ApplicationImpl error = new ApplicationImpl();
        terminado.setDistinguishedName("error");

        ApplicationImpl root  = new ApplicationImpl();
        root.setDistinguishedName(home);
        root.setProcessValues(Arrays.asList(cargar));

        root.setChildrenValues(Arrays.asList( (ServiceManifest)trabajo,ilegal));

        trabajo.setChildrenValues(Arrays.asList((ServiceManifest)terminado, error));


        CatalogCreateRequestImpl action  = new CatalogCreateRequestImpl(root, Application.CATALOG);
        action.setFollowReferences(true);

        return container.fireEvent(action);
    }


}
