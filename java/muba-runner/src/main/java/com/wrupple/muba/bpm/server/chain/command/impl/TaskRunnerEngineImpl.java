package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.SelectSolution;
import com.wrupple.muba.bpm.server.chain.command.SolveTask;
import com.wrupple.muba.bpm.server.chain.command.SynthesizeSolutionEntry;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

/**
 * Created by rarl on 11/05/17.
 */
public class TaskRunnerEngineImpl extends ChainBase implements TaskRunnerEngine {

    public TaskRunnerEngineImpl(//4. the actual solving of the problem to the chain
                                SolveTask findSolutions,
                                //5. Minimize Error
                                SelectSolution pickBestValue,
                                //6. synthesize Solution entry
                                SynthesizeSolutionEntry synthesize) {
        super(new Command []{
                findSolutions,
                pickBestValue,
                synthesize}
                );
    }
}
