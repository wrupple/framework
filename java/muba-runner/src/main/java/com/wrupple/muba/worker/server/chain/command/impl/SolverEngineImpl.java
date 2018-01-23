package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.SolverEngine;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.worker.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolverEngineImpl extends ChainBase implements SolverEngine {

    @Inject
    public SolverEngineImpl(

            // 2. Create variables,including contextual variables defined by previous task's saveToField
            //    by default use all variables defined in task
            DetermineSolutionFieldsDomain defineVariablesPossibilitySpace,
            // 3. Post constraints
            DefineSolutionCriteria defineProblem,
            //4. the actual solving of the problem to the chain
            SolveTask findSolutions) {
        super(new Command[]{
                defineVariablesPossibilitySpace,
                defineProblem,
                findSolutions}
        );
    }
}
