package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SelectSolution;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.chain.command.SynthesizeSolutionEntry;
import com.wrupple.muba.worker.server.service.impl.Callback;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SolveTaskCallbackImpl extends Callback<ApplicationContext> implements SolveTask.Callback {


    @Inject
    public SolveTaskCallbackImpl(
            //5. Minimize Error
            SelectSolution pickBestValue,
            //6. synthesize Solution entry
            SynthesizeSolutionEntry synthesize) {
        super(new Command[]{
                pickBestValue,
                synthesize}
        );
    }

}
