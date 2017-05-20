package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.PlainTextHumanSolver;
import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;

/**
 * Created by rarl on 19/05/17.
 */
public class PlainTextHumanSolverImpl extends ChainBase implements PlainTextHumanSolver {
    @Inject
    public PlainTextHumanSolverImpl(UserInteractionState writerCommand) {
        super(new Command[]{
                findSolutions,
                writerCommand,
                synthesize}
        );
    }
}
