package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ImplicitIntent;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;

/**
 * Created by japi on 29/07/17.
 */
public interface IntentResolverContext extends ImplicitIntent,ServiceContext {
    void setExcecutionContext(RuntimeContext requestContext);
}
