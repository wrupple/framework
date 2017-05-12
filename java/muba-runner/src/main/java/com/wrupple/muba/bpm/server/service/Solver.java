package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ActivityContext;

/**
 * Created by rarl on 11/05/17.
 */
public interface Solver {
    <T> T resolveProblemContext(ActivityContext context);
}
