package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class TaskRunnerEngineImpl extends ChainBase implements TaskRunnerEngine {

    @Inject
    public TaskRunnerEngineImpl(
            // 2. Create variables,including contextual variables defined by previous task's saveToField
            //    by default use all variables defined in task
            DetermineSolutionFieldsDomain defineVariablesPossibilitySpace,
            // 3. Post constraints
            DefineSolutionCriteria defineProblem,
            //4. the actual solving of the problem to the chain
                                SolveTask findSolutions,
                                //5. Minimize Error
                                SelectSolution pickBestValue,
                                //6. synthesize Solution entry
                                SynthesizeSolutionEntry synthesize) {
        super(new Command []{
                        defineVariablesPossibilitySpace,
                        defineProblem,
                        findSolutions,
                        pickBestValue,
                        synthesize}
                );
    }
}
