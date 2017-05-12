package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.server.service.Solver;
import org.chocosolver.solver.Model;

/**
 * Created by rarl on 11/05/17.
 */
public class SolverImpl implements Solver {
    private static final String MODEL_KEY = "com.wrupple.muba.solver.model";

    @Override
    public <T> T resolveProblemContext(ActivityContext context) {
        Model model = (Model) context.get(MODEL_KEY);
        if(model==null){
            model = createSolverModel(context);
            context.put(MODEL_KEY,model);
        }
        return (T)model;
    }

    private Model createSolverModel(ActivityContext context) {
        Model model = new Model(context.getDistinguishedName());
        return model;
    }
}
