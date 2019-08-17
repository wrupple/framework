package com.wrupple.muba.worker;



import com.wrupple.muba.event.ServiceBus;
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

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.*;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.IntentImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.domain.impl.WorkflowImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class BatchDriverAssignation extends WorkerTest {


    private RuntimeContext runtimeContext;

    DriverBooking booking;

    @Test
    public void submitBookingData() throws Exception {
        setUp();
        log.trace("[-Ask BPM what application item to use to handle this booking-]");

        //necesary to set an implicit instance to avoid inheritance
        runtimeContext.setServiceContract(new DriverBooking());
        runtimeContext.setSentence(IntentResolverServiceManifest.SERVICE_NAME,DriverBooking.class.getSimpleName(),DriverBooking.class.getSimpleName());

        runtimeContext.process();

        //THE RESULT OF PROCESING AN IMPLICIT INTENT IS AN EXPLICIT INTENT
        Invocation item = runtimeContext.getConvertedResult();
        assertTrue("a resolver must be found",item!=null);

        runtimeContext.getServiceBus().fireHandler(item, runtimeContext.getSession());


        log.trace("[-Create DriverBooking Handling Application Context-]");

        //item+booking;
        IntentImpl bookingRequest = new IntentImpl();
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

        bookingRequest = new IntentImpl();
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

        booking = (DriverBooking) activityState.getEntryValue();
        assertTrue(booking.getStakeHolder()!=null);
        assertTrue(booking.getDriverValue()!=null);
        assertTrue(Math.abs(booking.getDriverValue().getLocation()-booking.getLocation())==1);
    }


    public void setUp() throws Exception {

        SessionContext session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM)));
        ServiceBus wrupple = injector.getInstance(ServiceBus.class);
        log.trace("NEW TEST EXCECUTION ENVIROMENT READY");
        runtimeContext = new RuntimeContextImpl( wrupple, session,null);
        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
        log.trace("[-register catalogs-]");

        // expectations

        replayAll();

        CatalogDescriptor contractDescriptor = injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(Contract.Event_CATALOG)));

        CatalogDescriptor bookingDescriptor = builder.fromClass(DriverBooking.class, DriverBooking.class.getSimpleName(),"DriverBooking", contractDescriptor);
        bookingDescriptor.setConsolidated(false);
        CatalogActionRequestImpl action = new CatalogCreateRequestImpl(bookingDescriptor,CatalogDescriptor.CATALOG_ID);
        action.setEntryValue(bookingDescriptor);
        action.setFollowReferences(true);
        runtimeContext.getServiceBus().fireEvent(action,runtimeContext,null);


        bookingDescriptor = (CatalogDescriptor) action.getResults().get(0);
        assertTrue("driver booking must have a parent type ",bookingDescriptor.getParentValue()!=null);
        assertTrue("driver booking must be a suptype of contract ",bookingDescriptor.getParentValue().getDistinguishedName().equals(Contract.Event_CATALOG));

        action.setEntryValue(builder.fromClass(Driver.class, Driver.class.getSimpleName(),
                "Driver", 1, null));
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.process();

        runtimeContext.reset();

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
        updateBooking.setCatalog(DriverBooking.class.getSimpleName());
        updateBooking.setName(CatalogActionRequest.WRITE_ACTION);


        log.trace("[-create booking data handling application item-]");
        ApplicationImpl item = new ApplicationImpl();

        item.setDistinguishedName("createTrip");
        item.setProcessValues(Arrays.asList(pickDriver,updateBooking));
        //this tells bpm to use this application to resolve bookings
        item.setCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputField("booking");

        action = new CatalogActionRequestImpl();
        action.setFollowReferences(true);
        action.setEntryValue(item);

        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, Application.CATALOG, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        item = catalogContext.getEntryResult();

        runtimeContext.reset();




        log.trace("[-create a pool of drivers to resolve the booking-]");

        Driver driver;
        for(long i = 0 ; i < 10 ; i++){
            driver = new Driver();
            //thus, best driver will have a location of 6, or 8 because 7 will not be available
            driver.setLocation(i);
            driver.setAvailable(i%2==0);

            action = new CatalogCreateRequestImpl(driver,Driver.CATALOG);

            runtimeContext.getServiceBus().fireEvent(action,runtimeContext,null);
        }

        log.trace("[-Create a DriverBooking-]");

        booking = new DriverBooking();
        booking.setLocation(7l);
        booking.setName("test");

        action = new CatalogCreateRequestImpl(booking,DriverBooking.class.getSimpleName());
        action.setFollowReferences(true);
        action.setEntryValue(booking);

        runtimeContext.getServiceBus().fireEvent(action,runtimeContext,null);




        runtimeContext  = new RuntimeContextImpl( wrupple, session,null);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }



    public static void main(String[] args) {
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
        /*
         Model model = new Model();
            IntVar x = model.intVar(0, 9);
            IntVar y = model.intVar(0, 9);
            IntVar z = model.intVar(0, 9);
            int r = 10;
            x.add(y).sub(z).eq(r).post();
            model.getSolver().showSolutions(
                    () -> String.format("%d + %d - %d = %d",
                            x.getValue(), y.getValue(), z.getValue(), r));
            model.getSolver().findAllSolutions();


            // The model is the main component of Choco Solver
             model = new Model("Choco Solver Hello World");
            // Integer variables
            IntVar a = model.intVar("a", new int[]{4, 6, 8}); // takes value in { 4, 6, 8 }
            IntVar b = model.intVar("b", 0, 2); // takes value in [0, 2]


            // Add an arithmetic constraint between a and b
            // BEWARE : do not forget to call post() to force this constraint to be satisfied
            //model.arithm(a, "+", b, "<", 8).post();
            a.add(b).lt(8).post();

            int i = 1;
            // Computes all solutions : Solver.solve() returns true whenever a new feasible solution has been found
            while (model.getSolver().solve()) {
                System.out.println("Solution " + i++ + " found : " + a + ", " + b);
            }
         */
}