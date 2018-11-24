package com.wrupple.muba.worker;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;

public class ForeignKeyAssignation {


    public static void main(String[] args) {
        int NUM_DRIVERS = 5;
        int[] LOCATIONS = new int[]{20, 2, 7, 20, 30};

        Model model = new Model("Driver key");

        IntVar[] driverLocations = model.intVarArray("driverLocations",NUM_DRIVERS,LOCATIONS);
        IntVar bookingLocation = model.intVar(7);
        IntVar foreignKeyAssignation = model.intVar("foreignKeyAssignation", 1, NUM_DRIVERS, false);
        IntVar bookingDistance = model.intVar("bookingDistance", 0, 100, true);


        // CONSTRAINTS
        IntVar[] distances = new IntVar[NUM_DRIVERS];

        for (int j = 0; j < NUM_DRIVERS; j++) {

            distances[j]=bookingLocation.sub(driverLocations[j]).abs().intVar();

        }

        model.element(bookingDistance, distances, foreignKeyAssignation, 1).post();
        model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, bookingDistance);

        Solver solver = model.getSolver();
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
