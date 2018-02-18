package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class StreamingIntentDelegate implements EventBusImpl.IntentDelegate {

    protected static final Logger log = LoggerFactory.getLogger(EventBusImpl.class);

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
    public List<Object> handleExplicitIntent(SessionContext session, RuntimeContext parentTimeline, List<Intent> handlers, EventBusImpl eventBus) throws Exception {
        if (parallel) {
            return handlers.parallelStream().map(handler -> {
                try {
                    eventBus.fireHandler(handler, session, parentTimeline);
                } catch (Exception e) {
                    handler.setError(e);
                    //implicitRequestContract.addError(e);
                    return handler;
                }
                return handler.getConvertedResult();
            }).collect(Collectors.toList());
        } else {
            RuntimeContextImpl runtimeContext = new RuntimeContextImpl(eventBus, session, parentTimeline);
            for (Intent handler : handlers) {
                log.info("[sequential invocation of handler] {}", handler);
                if (eventBus.fireHandlerWithRuntime(handler, runtimeContext) != Command.CONTINUE_PROCESSING) {
                    log.warn("[handler broke sequential invocation chain] {}", handler);
                    break;
                }
            }
            return runtimeContext.getConvertedResult();
        }


    }

    @Override
    public List<Intent> interpretImlicitIntent(Event implicitRequestContract, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline, List<ServiceManifest> manifests, Instrospection introspector, EventBusImpl eventBus) {
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
