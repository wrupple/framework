package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class IntentDelegateImpl implements EventBusImpl.IntentDelegate {
    protected static final Logger log = LoggerFactory.getLogger(EventBusImpl.class);

    @Override
    public List<Object> handleExplicitIntent(SessionContext session, RuntimeContext parentTimeline, List<ExplicitIntent> handlers, EventBusImpl eventBus) throws Exception {

        RuntimeContextImpl runtimeContext = new RuntimeContextImpl(eventBus, session, parentTimeline);
        for (ExplicitIntent handler : handlers) {
            log.info("[sequential invocation of handler] {}", handler);
            if (eventBus.fireHandlerWithRuntime(handler, runtimeContext) != Command.CONTINUE_PROCESSING) {
                log.warn("[handler broke sequential invocation chain] {}", handler);
                break;
            }
        }
        return runtimeContext.getConvertedResult();
    }

    @Override
    public List<ExplicitIntent> interpretImlicitIntent(Event implicitRequestContract, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline, List<ServiceManifest> manifests, Instrospection introspector, EventBusImpl eventBus) {

        List<ExplicitIntent> regreso = new ArrayList<>(manifests.size());
        ExplicitIntent intent;
        for (ServiceManifest manifest : manifests) {
            intent = eventBus.getIntentInterpret().resolveIntent(implicitRequestContract, manifest, parentTimeline);
            if (intent != null) {
                regreso.add(intent);
            }
        }


        return regreso;
    }
}
