package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.command.DefineSolutionCriteria;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rarl on 29/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {
    protected Logger log = LoggerFactory.getLogger(DefineSolutionCriteriaImpl.class);

    @Override
    public boolean execute(Context context) throws Exception {
        log.info("Defining solution criteria");
        return CONTINUE_PROCESSING;
    }
}
