package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by rarl on 29/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {
    protected Logger log = LogManager.getLogger(DefineSolutionCriteriaImpl.class);

    @Override
    public boolean execute(Context context) throws Exception {
        log.info("no interface to provide human runner setRuntimeContext solution criteria");
        //unless theres a universal way to post criteria to the human this is as far as we go as to the definition of the problem
        return CONTINUE_PROCESSING;
    }
}
