package com.wrupple.muba.worker;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;

public class RideDriverAssignation {


    public static void main(String[] args) {
        int NUM_DRIVERS = 5;
        final int TARGET_POSITION = 7;
        int[] POSITIONS = new int[]{20, 2, 7, 20, 30};

        Model model = new Model("Driver assignation");

        IntVar assignation = model.intVar("assignation", 1, NUM_DRIVERS, false);
        IntVar distance = model.intVar("distance", 0, 100, true);

        IntVar[] distances = new IntVar[NUM_DRIVERS];

        for (int j = 0; j < NUM_DRIVERS; j++) {
            distances[j]=model.intAbsView(model.intVar(POSITIONS[j]-TARGET_POSITION));
        }
        // CONSTRAINTS

        model.element(distance, distances, assignation, 1).post();
        model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, distance);

        Solver solver = model.getSolver();
        solver.showShortStatistics();

        while (solver.solve()) {
            prettyPrint(model, NUM_DRIVERS, new IntVar[]{assignation}, 1, distance);
        }
    }

    private static void prettyPrint(Model model, int NUM_DRIVERS, IntVar[] assignation, int PASSENGERS, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < NUM_DRIVERS; i++) {
                st.append(String.format("\tDriver %d picks up passenger : ", (i + 1)));
                for (int j = 0; j < PASSENGERS; j++) {
                    if (assignation[j].getValue() == (i + 1)) {
                        st.append(String.format("psngr: %d ", (j + 1)));
                    }
                }
                st.append("\n");

        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }

}
