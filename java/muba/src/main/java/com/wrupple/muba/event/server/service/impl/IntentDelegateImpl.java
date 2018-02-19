package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class IntentDelegateImpl implements ServiceBusImpl.IntentDelegate {
    protected static final Logger log = LoggerFactory.getLogger(ServiceBusImpl.class);

    @Override
    public List<Object> handleExplicitIntent(SessionContext session, RuntimeContext parentTimeline, List<Invocation> handlers, ServiceBusImpl eventBus) throws Exception {

        RuntimeContextImpl runtimeContext = new RuntimeContextImpl(eventBus, session, parentTimeline);
        for (Invocation handler : handlers) {
            log.info("[sequential invocation of handler] {}", handler);
            eventBus.fireHandlerWithRuntime(handler, runtimeContext);
        }
        return runtimeContext.getConvertedResult();
    }

    @Override
    public List<Invocation> interpretImlicitIntent(Contract implicitRequestContract, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline, List<ServiceManifest> manifests, Instrospection introspector, ServiceBusImpl eventBus) {

        List<Invocation> regreso = new ArrayList<>(manifests.size());
        Invocation invocation;
        for (ServiceManifest manifest : manifests) {
            invocation = eventBus.getIntentInterpret().resolveIntent(implicitRequestContract, manifest, parentTimeline);
            if (invocation != null) {
                regreso.add(invocation);
            }
        }


        return regreso;
    }
}
