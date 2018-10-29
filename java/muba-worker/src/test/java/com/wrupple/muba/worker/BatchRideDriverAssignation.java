package com.wrupple.muba.worker;


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

public class BatchRideDriverAssignation {

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
