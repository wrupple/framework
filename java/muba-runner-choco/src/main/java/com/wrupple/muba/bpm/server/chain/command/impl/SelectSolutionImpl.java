package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.command.SelectSolution;
import org.apache.commons.chain.Context;

/**
 * Created by rarl on 11/05/17.
 */
public class SelectSolutionImpl implements SelectSolution {
    @Override
    public boolean execute(Context context) throws Exception {
        return CONTINUE_PROCESSING;
    }
}
