package com.wrupple.muba.worker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.desktop.domain.impl.WorkerContractImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.worker.domain.RiderBooking;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.junit.Test;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class ForeignKeyAssignationTest extends WorkerTest {


    @Test
    public void submitBookingData() throws Exception {

        // expectations
        replayAll();
        defineModel();

        log.info("         [-create application tree-]");
        ApplicationImpl root = createApplication(container,HOME);

        assertTrue("Application tree not created",!root.getChildrenValues().get(0).getChildrenValues().isEmpty());

        log.info("         [-create a pool of drivers to resolve the riderBooking-]");

        createMockDrivers();

        log.info("         [-Create a DriverBooking-]");

        RiderBooking riderBooking = new RiderBooking();
        riderBooking.setLocation(7l);
        riderBooking.setName("test");

        CatalogActionRequest action = new CatalogCreateRequestImpl(riderBooking, RiderBooking.class.getSimpleName());
        action.setFollowReferences(true);
        assertTrue(riderBooking.getId()==null);
        riderBooking =  container.fireEvent(action);
        assertTrue(riderBooking.getId()!=null);
        action = new CatalogReadRequestImpl(riderBooking.getId(),RiderBooking.class.getSimpleName());
        riderBooking =  container.fireEvent(action);
        //assertTrue(riderBooking.getTimestamp()!=null);

        Host hostValue = container.getInjector().getInstance(Key.get(Host.class,Names.named(SessionContext.SYSTEM)));

        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.info("         [-use riderBooking id to launch container with previously created riderBooking -]");


        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        Map<String, LoggerConfig> loggers = config.getLoggers();
        loggers.get("org.apache.commons.chain.impl.ChainBase").setLevel(Level.DEBUG);
        loggers.get("com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl").setLevel(Level.TRACE);
        ctx.updateLoggers();

        container.fireEvent(new WorkerContractImpl(
                Arrays.asList(":"+riderBooking.getId().toString()),
                HOME,
                hostValue));
        //check conditions
        assertTrue(riderBooking.getDriverValue()!=null);
    }




    public static void main(String[] args) {

        Model model = new Model("Driver key");

        IntVar bookingLocation = model.intVar(7);
        IntVar bookingDistance = model.intVar("bookingDistance", 0, 100, true);
        model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, bookingDistance);


        // CONSTRAINTS
        int NUM_DRIVERS = 5;
        int[] LOCATIONS = new int[]{20, 7, 2, 20, 30};

        IntVar[] driverLocations = model.intVarArray("driverLocations",NUM_DRIVERS,LOCATIONS);

        IntVar[] distances = new IntVar[NUM_DRIVERS];

        for (int j = 0; j < NUM_DRIVERS; j++) {

            distances[j]=bookingLocation.sub(driverLocations[j]).abs().intVar();

        }
        IntVar foreignKeyAssignation = model.intVar("foreignKeyAssignation", 1, NUM_DRIVERS, false);

        model.element(bookingDistance, distances, foreignKeyAssignation, 1).post();

        Solver solver = model.getSolver();
        System.out.println(model);

        solver.showShortStatistics();

        while (solver.solve()) {
            prettyPrint(model, NUM_DRIVERS, new IntVar[]{foreignKeyAssignation}, 1, bookingDistance);
        }
    }

    private static void prettyPrint(Model model, int NUM_DRIVERS, IntVar[] assignation, int PASSENGERS, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < NUM_DRIVERS; i++) {
            for (int j = 0; j < PASSENGERS; j++) {
                if (assignation[j].getValue() == (i + 1)) {
                    st.append(String.format("\tDriver %d picks up passenger : ", (i + 1)));
                    st.append(String.format("psngr: %d ", (j + 1)));
                }
            }
            st.append("\n");

        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }
}
