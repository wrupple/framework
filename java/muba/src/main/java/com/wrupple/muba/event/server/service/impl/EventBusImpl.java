package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.IntrospectionStrategy;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.impl.ContextBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EventBusImpl extends ContextBase implements EventBus {


    private final IntentDelegate delegate;


    protected static final Logger log = LoggerFactory.getLogger(EventBusImpl.class);

	private static final long serialVersionUID = -7144539787781019055L;

	private final PrintWriter outputWriter;
    private final OutputStream out;
    private final InputStream in;
	private final EventRegistry intentInterpret;
    private final EventDispatcher process;

    private final IntrospectionStrategy instrospector;
    private final Provider<BroadcastEvent> queueElementProvider;
    @Inject
    public EventBusImpl(EventRegistry intentInterpret, EventDispatcher process, @Named("System.out") OutputStream out, @Named("System.in") InputStream in, FieldAccessStrategy instrospector, Provider<BroadcastEvent> queueElementProvider, IntentDelegate delegate) {
        super();
        this.process=process;
        this.out=out;
        this.in = in;
        this.outputWriter = new PrintWriter(out);
        this.intentInterpret = intentInterpret;
        this.instrospector = instrospector;

        this.queueElementProvider = queueElementProvider;
        this.delegate = delegate;
    }

    boolean fireHandlerWithRuntime(ExplicitIntent event, RuntimeContext runtimeContext) throws Exception {
        runtimeContext.setSentence(event.getSentence());
        runtimeContext.setServiceContract(event.getImplicitIntentValue());
        boolean regreso = resume(runtimeContext);
        event.setResult(runtimeContext.getResults());
        return regreso;
    }

    //TODO public UserTransaction getTransaction() {



    @Override
    public OutputStream getOutput() {
        return out;
    }

    @Override
    public InputStream getInput() {
        return in;
    }

    public PrintWriter getOutputWriter() {
		return outputWriter;
	}



    @Override
    public EventRegistry getIntentInterpret() {
        return intentInterpret;
    }

    @Override
    public void broadcastEvent(Event event, RuntimeContext runtimeContext, List<FilterCriteria> explicitlySuscriptedObservers) throws Exception {
        BroadcastEvent queued = queueElementProvider.get();
        queued.setEventValue(event);
        queued.setObserversValues(explicitlySuscriptedObservers);
        queued.setDomain(event.getDomain());
        fireEvent(queued,runtimeContext,null/*use all broadcast handlers*/);
    }

    @Override
    public boolean fireHandler(ExplicitIntent event, ContainerContext session) throws Exception {
        return fireHandler(event,session,null);
    }


    public boolean fireHandler(ExplicitIntent event, ContainerContext session, RuntimeContext parentTimeline) throws Exception {
        RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,parentTimeline);
        return fireHandlerWithRuntime(event,runtimeContext);
    }

    @Override
    public <T> T fireEvent(Event implicitRequestContract, RuntimeContext parent, List<FilterCriteria> handlerCriterion) throws Exception {
        return fireonRuntimeline(implicitRequestContract,parent.getSession(),handlerCriterion,parent);
    }

    public <T> T fireonRuntimeline(Event implicitRequestContract, ContainerContext session, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline) throws Exception {
        List<ServiceManifest> manifests = getIntentInterpret().resolveHandlers(implicitRequestContract.getCatalogType());

        if(manifests==null || manifests.isEmpty()){

            throw new IllegalArgumentException("no handlers for event "+implicitRequestContract.getCatalogType());

        }else{
            log.trace("[Count of matching services] {}",manifests.size());
            Instrospection introspector=instrospector.newSession(manifests.get(0));
            List<ExplicitIntent> handlers = delegate.interpretImlicitIntent(implicitRequestContract, handlerCriterion, parentTimeline, manifests, introspector, this);


            if(handlers==null || handlers.isEmpty()){
                log.error("No known handlers for event {}",implicitRequestContract);
                throw new IllegalArgumentException("no handlers for event "+implicitRequestContract.getCatalogType());
            } else if(handlers.size()==1){
                log.info("[single handler invocation]");
                ExplicitIntent call = handlers.get(0);
                fireHandler(call,session,parentTimeline);
                return call.getConvertedResult();
            } else {

                log.info("[parallel invocation of handlers]");
                List<Object> results = delegate.handleExplicitIntent(session, parentTimeline, handlers, this);
                return (T) results;

            }
        }
    }

    @Override
    public <T> T fireEvent(Event implicitRequestContract, ContainerContext session, List<FilterCriteria> handlerCriterion) throws Exception {
        return fireonRuntimeline(implicitRequestContract, session, handlerCriterion, null);
    }

    public interface IntentDelegate {
        List<Object> handleExplicitIntent(ContainerContext session, RuntimeContext parentTimeline, List<ExplicitIntent> handlers, EventBusImpl eventBus) throws Exception;

        List<ExplicitIntent> interpretImlicitIntent(Event implicitRequestContract, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline, List<ServiceManifest> manifests, Instrospection introspector, EventBusImpl eventBus);

    }


    @Override
    public boolean resume(RuntimeContext runtimeContext) throws Exception {
        return process.execute(runtimeContext);
    }
    Map<String,NaturalLanguageInterpret> interpretMap;
    private Map<String,NaturalLanguageInterpret> getInterpretMap(){
        if(interpretMap==null){
            interpretMap= new HashMap<>();
        }
        return interpretMap;
    }

    @Override
    public boolean hasInterpret(String next) {
        return getInterpretMap().containsKey(next);
    }

    @Override
    public NaturalLanguageInterpret getInterpret(String next) {
        return getInterpretMap().get(next);
    }

    @Override
    public void registerInterpret(String constraint, NaturalLanguageInterpret constraintInterpret) {
        getInterpretMap().put(constraint, constraintInterpret);
    }
}
