package com.wrupple.muba.bootstrap.server.service;

import com.wrupple.muba.bootstrap.domain.ExplicitIntent;
import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;

/**
 * Created by japi on 22/08/17.
 */
public interface ImplicitEventResolver {


    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception;

}
