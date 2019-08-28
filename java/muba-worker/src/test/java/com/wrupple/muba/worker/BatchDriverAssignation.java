package com.wrupple.muba.worker;



import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.worker.domain.impl.*;
import junit.framework.TestCase;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMiddle;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.Arrays;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class BatchDriverAssignation extends WorkerTest {


    private RuntimeContext runtimeContext;

    RiderBooking booking;

    @Test
    public void submitBookingData() throws Exception {
        setUp();
        log.trace("[-Ask BPM what application item to use to handle this booking-]");

        runtimeContext.setSentence(IntentResolverServiceManifest.SERVICE_NAME,String.valueOf(CatalogEntry.PUBLIC_ID),RiderBooking.class.getSimpleName());

        runtimeContext.process();

        Invocation item = runtimeContext.getConvertedResult();
        assertTrue("an invocation must be returned",item!=null);
        IntentImpl bookingRequest = (IntentImpl) item.getEventValue();
        assertTrue("a resolver must be found",item!=null);
        ApplicationState activityState = bookingRequest.getStateValue();
        assertTrue("an application state bage must be created",activityState!=null);
        String applicationId = activityState.getApplicationValue().getDistinguishedName();
        assertTrue("Application metadata must be attached to application state",applicationId!=null);

        //item+booking;

        WorkerStateImpl state = new WorkerStateImpl();
        state.setStateValue(activityState);
        activityState.setWorkerStateValue(state);
        bookingRequest.getStateValue().setWorkerStateValue(state);
        runtimeContext.getServiceBus().fireEvent(bookingRequest,runtimeContext,null);
        //a new activity state


        Long firstTask = activityState.getTaskDescriptor();

        assertTrue("First task has been assigned",firstTask!=null);

        log.info("Attempt to find the best available driver");
        activityState.setEntryValue(booking);
        runtimeContext.getServiceBus().fireEvent(activityState,runtimeContext,null);


        item = runtimeContext.getConvertedResult();

        log.info("manually assinging solution");
        bookingRequest = (IntentImpl) item.getEventValue();
        bookingRequest.setEntryValue(activityState.getEntryValue());


        log.info("post solution to the business engine");

        activityState = runtimeContext.getServiceBus().fireEvent(bookingRequest,runtimeContext,null);

        booking = (RiderBooking) activityState.getEntryValue();
        assertTrue(booking.getStakeHolder()!=null);
        assertTrue(booking.getDriverValue()!=null);
    }


    public void setUp() throws Exception {

        // expectations
        replayAll();
        defineModel();

        log.info("         [-create application tree-]");
        ApplicationImpl root = createApplication(container,HOME);

        TestCase.assertTrue("Application tree not created",!root.getChildrenValues().get(0).getChildrenValues().isEmpty());

        log.info("         [-create a pool of drivers to resolve the riderBooking-]");

        createMockDrivers();

        log.trace("[-Create a DriverBooking-]");

        booking = new RiderBooking();
        booking.setLocation(7l);
        booking.setName("test");

        CatalogCreateRequestImpl action = new CatalogCreateRequestImpl(booking, RiderBooking.class.getSimpleName());
        action.setFollowReferences(true);
        action.setEntryValue(booking);

        SessionContext session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM)));
        ServiceBus wrupple = injector.getInstance(ServiceBus.class);
        runtimeContext  = new RuntimeContextImpl( wrupple, session,null);

        booking = runtimeContext.getServiceBus().fireEvent(action,runtimeContext,null);

        assertTrue("booking not created",booking!=null);



        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }



    @Test
    public void batchProcessingIntent() {
        // load parameters
// number of warehouses
        int NUM_DRIVERS = 5;
// number of passengers
        int NUM_PASSENGERS = 2;
// matrix of driving distance passanger x driver
        int[][] DISTANCES = new int[][]{
                {20, 24, 11, 2, 30},
                {28, 27, 82, 83, 74},
                {74, 97, 71, 96, 70},
                {2, 55, 73, 69, 61},{61, 69, 73,55 ,2 }};

// A new model instance
        Model model = new Model("Driver assignation");

// VARIABLES
// a driver is either assigned or unassigned
        BoolVar[] assigned = model.boolVarArray("o", NUM_DRIVERS);
// which driver will pickup passanger
        IntVar[] assignation = model.intVarArray("assignation", NUM_PASSENGERS, 1, NUM_DRIVERS, false);
// ride distance per passenger
        IntVar[] distance = model.intVarArray("distance", NUM_PASSENGERS, 1, 96, true);
// Total of all costs
        IntVar tot_cost = model.intVar("tot_cost", 0, 99999, true);

// CONSTRAINTS
        for (int j = 0; j < NUM_PASSENGERS; j++) {
            // a driver is 'assigned' to a passenger
            model.element(model.intVar(1), assigned, assignation[j], 1).post();
            // Compute 'distance' for each passenger
            //Creates a element constraint: distance = distances[assignation-offset] .
            model.element(distance[j], DISTANCES[j], assignation[j], 1).post();
        }

        int capacity =  1;

        for (int i = 0; i < NUM_DRIVERS; i++) {
            // additional variable 'occ' is created on the fly
            // its domain includes the constraint on capacity
            IntVar occ = model.intVar("occur_" + i, 0, capacity, true);
            // for-loop starts at 0, warehouse index starts at 1
            // => we count occurrences of (i+1) in 'assignation'
            model.count(i + 1, assignation, occ).post();
            // redundant link between 'occ' and 'open' for better propagation
            occ.ge(assigned[i]).post();
        }

// Prepare the constraint that maintains 'tot_cost'
        int[] coeffs = new int[ NUM_PASSENGERS];
        Arrays.fill(coeffs, 0,  NUM_PASSENGERS, 1);
// then post it
        model.scalar(distance, coeffs, "=", tot_cost).post();

        model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, tot_cost);
        Solver solver = model.getSolver();
        solver.setSearch(Search.intVarSearch(
                new VariableSelectorWithTies<>(
                        new FirstFail(model),
                        new Smallest()),
                new IntDomainMiddle(false),
                ArrayUtils.append(assignation, distance, assigned))
        );
        solver.showShortStatistics();
        while (solver.solve()) {
            prettyPrint(model, assigned, NUM_DRIVERS, assignation, NUM_PASSENGERS, tot_cost);
        }
    }

    private static void prettyPrint(Model model, IntVar[] assigned, int NUM_DRIVERS, IntVar[] assignation, int PASSENGERS, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < NUM_DRIVERS; i++) {
            if (assigned==null||assigned[i].getValue() > 0) {
                st.append(String.format("\tDriver %d picks up passenger : ", (i + 1)));
                for (int j = 0; j < PASSENGERS; j++) {
                    if (assignation[j].getValue() == (i + 1)) {
                        st.append(String.format("psngr: %d ", (j + 1)));
                    }
                }
                st.append("\n");
            }
        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }

}