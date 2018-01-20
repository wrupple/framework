package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

    private final ProcessManager plugin;
    private final Callback callback;

    @Inject
    public SolveTaskImpl(ProcessManager plugin, Callback callback) {
        this.plugin = plugin;
        this.callback = callback;
    }
    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        log.info("Thinking synchronously...");

        if (plugin.getSolver().solve(context, callback) == CONTINUE_PROCESSING) {
            return CONTINUE_PROCESSING;
        } else {
            throw new IllegalStateException("No viable solution found for problem");
        }
    }
}
