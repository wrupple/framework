package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.server.service.Solver;

/**
 * Created by rarl on 26/05/17.
 */
public class HumanSolverImpl implements Solver {
    @Override
    public <T> T resolveProblemContext(ActivityContext context) {
        return (T) context;
    }
}
