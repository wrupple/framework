package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.ServiceContext;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 29/07/17.
 */
public interface IntentResolverContext extends ImplicitIntent,ServiceContext {
    void setExcecutionContext(RuntimeContext requestContext);
}
