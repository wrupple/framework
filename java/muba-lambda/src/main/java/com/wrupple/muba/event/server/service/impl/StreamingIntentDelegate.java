package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class StreamingIntentDelegate implements ServiceBusImpl.IntentDelegate {

    protected static final Logger log = LoggerFactory.getLogger(ServiceBusImpl.class);

    private final CatalogDescriptor handleField;

    private final FilterNativeInterface filterer;
    private final boolean parallel;

    @Inject
    public StreamingIntentDelegate(@Named("event.parallel") Boolean parallel, FilterNativeInterface filterer, @Named(ServiceManifest.CATALOG) CatalogDescriptor handleField) {
        this.handleField = handleField;
        this.filterer = filterer;
        this.parallel = parallel;
    }

    @Override
    public List<Object> handleExplicitIntent(SessionContext session, RuntimeContext parentTimeline, List<Invocation> handlers, ServiceBusImpl eventBus) throws Exception {
        if (parallel) {
            return handlers.parallelStream().map(handler -> {
                try {
                    return eventBus.fireHandler(handler, session, parentTimeline);
                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
            }).collect(Collectors.toList());
        } else {
            RuntimeContextImpl runtimeContext = new RuntimeContextImpl(eventBus, session, parentTimeline);
            for (Invocation handler : handlers) {
                log.info("[sequential invocation of handler] {}", handler);
                eventBus.fireHandlerWithRuntime(handler, runtimeContext);
            }
            return runtimeContext.getConvertedResult();
        }


    }

    @Override
    public List<Invocation> interpretImlicitIntent(Contract implicitRequestContract, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline, List<ServiceManifest> manifests, Instrospection introspector, ServiceBusImpl eventBus) {
        return manifests.stream().
                filter(handler -> {
                    if (handlerCriterion == null || handleField == null || handlerCriterion.isEmpty()) {
                        return true;
                    } else {
                        return filterer.matchAgainstFilters(handler, handlerCriterion, handleField, introspector);
                    }
                }).
                map(manifest -> eventBus.getIntentInterpret().resolveIntent(implicitRequestContract, manifest, parentTimeline)).filter(explicitIntent -> explicitIntent != null)
                .collect(Collectors.toList());
    }


}
