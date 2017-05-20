package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.SelectSolution;
import com.wrupple.muba.bpm.server.chain.command.SolveTask;
import com.wrupple.muba.bpm.server.chain.command.SynthesizeSolutionEntry;
import com.wrupple.vegetate.server.chain.AsyncTaskRunnerEngine;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 19/05/17.
 */
@Singleton
public class DelegatingTaskRunnerEngine extends ChainBase implements TaskRunnerEngine {

    @Inject
    public DelegatingTaskRunnerEngine(SearchEngineOptimizedDesktopWriterCommand writerCommand) {
        super(new Command[]{
                findSolutions,
                writerCommand,
                synthesize}
        );
    }
}
