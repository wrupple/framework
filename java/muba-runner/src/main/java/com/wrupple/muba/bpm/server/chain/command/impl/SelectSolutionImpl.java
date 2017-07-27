package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.command.SelectSolution;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SelectSolutionImpl implements SelectSolution {

    protected Logger log = LoggerFactory.getLogger(SelectSolutionImpl.class);

    private final SolverCatalogPlugin plugin;

    @Inject
    public SelectSolutionImpl(SolverCatalogPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        log.info("Selecting best solution");
        //ApplicationContext context = (ApplicationContext) ctx;
        return CONTINUE_PROCESSING;
    }
}
