package com.wrupple.muba.event.server.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import com.wrupple.muba.event.server.service.IntrospectionStrategy;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ContextBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class EventBusImpl extends ContextBase implements EventBus {
    protected static final Logger log = LoggerFactory.getLogger(EventBusImpl.class);

	private static final long serialVersionUID = -7144539787781019055L;

	private final PrintWriter outputWriter;
    private final OutputStream out;
    private final InputStream in;
	private final EventRegistry intentInterpret;
    private final EventDispatcher process;
	private final Provider<UserTransaction> transactionProvider;
    private final Boolean parallel;
    private final CatalogDescriptor handleField;
    private UserTransaction transaction;

    private final FilterNativeInterface filterer;

    private final IntrospectionStrategy instrospector;
    private final Provider<EventBroadcastQueueElement> queueElementProvider;

    @Inject
	public EventBusImpl(EventRegistry intentInterpret, EventDispatcher process, @Named("System.out") OutputStream out, @Named("System.in") InputStream in, @Named("event.parallel") Boolean parallel, Provider<UserTransaction> transactionProvider, FilterNativeInterface filterer, @Named(ServiceManifest.CATALOG) CatalogDescriptor handleField, FieldAccessStrategy instrospector, Provider<EventBroadcastQueueElement> queueElementProvider) {
		super();
		this.parallel=parallel;
		this.process=process;
		this.out=out;
		this.in = in;
		this.outputWriter = new PrintWriter(out);
        this.intentInterpret = intentInterpret;
        this.transactionProvider=transactionProvider;
        this.filterer = filterer;

        this.handleField=handleField;
        this.instrospector = instrospector;

        this.queueElementProvider = queueElementProvider;
    }

    @Override
    public UserTransaction getTransaction() {
        if (transaction == null) {
            transaction = transactionProvider == null ? null : transactionProvider.get();
        }
        return transaction;
    }




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
        EventBroadcastQueueElement queued = queueElementProvider.get();
        queued.setEventValue(event);
        queued.setObserversValues(explicitlySuscriptedObservers);
        fireEvent(queued,runtimeContext,null/*use all broadcast handlers*/);
    }

    @Override
    public boolean fireHandler(ExplicitIntent event, SessionContext session) throws Exception {
        return fireHandler(event,session,null);
    }


    public boolean fireHandler(ExplicitIntent event, SessionContext session, RuntimeContext parentTimeline) throws Exception {
        RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,parentTimeline);
        return fireHandlerWithRuntime(event,runtimeContext);
    }

    @Override
    public <T> T fireEvent(Event implicitRequestContract, RuntimeContext parent, List<FilterCriteria> handlerCriterion) throws Exception {
        return fireonRuntimeline(implicitRequestContract,parent.getSession(),handlerCriterion,parent);
    }

    private boolean fireHandlerWithRuntime(ExplicitIntent event, RuntimeContext runtimeContext) throws Exception {
        runtimeContext.setSentence(event.getSentence());
        runtimeContext.setServiceContract(event.getState());
        boolean regreso = resume(runtimeContext);
        event.setResult(runtimeContext.getResult());
        return regreso;
    }

    @Override
    public <T> T fireEvent(Event implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion) throws Exception {
       return fireonRuntimeline(implicitRequestContract,session,handlerCriterion,null);
    }

    public <T> T fireonRuntimeline(Event implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion, RuntimeContext parentTimeline) throws Exception {
        List<ServiceManifest> manifests = getIntentInterpret().resolveHandlers(implicitRequestContract.getCatalogType());

        if(manifests==null || manifests.isEmpty()){

            throw new IllegalArgumentException("no handlers for event "+implicitRequestContract.getCatalogType());

        }else{

            Instrospection introspector=instrospector.newSession(manifests.get(0));
            List<ExplicitIntent> handlers =  manifests.stream().
                    filter(handler -> {
                        if(handlerCriterion==null||handleField==null||handlerCriterion.isEmpty()){
                            return true;
                        }else{
                            return filterer.matchAgainstFilters(handler, handlerCriterion, handleField, introspector);
                        }
                    }).
                    map(manifest -> intentInterpret.resolveIntent(implicitRequestContract, manifest, parentTimeline)).
                    collect(Collectors.toList());


            if(handlers==null || handlers.isEmpty()){
                log.error("No known handlers for event {}",implicitRequestContract);
                return (T) implicitRequestContract;
            } else if(handlers.size()==1){
                ExplicitIntent call = handlers.get(0);
                fireHandler(call,session,parentTimeline);
                return call.getConvertedResult();
            }else if(parallel){
                List<Object> results=  handlers.parallelStream().map(handler -> {
                    try {
                        fireHandler(handler,session,parentTimeline);
                    } catch (Exception e) {
                        handler.setError(e);
                        //implicitRequestContract.addError(e);
                        return handler;
                    }
                    return handler.getConvertedResult();
                }).collect(Collectors.toList());
                return (T) results;
            }else{
                RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,parentTimeline);
                for(ExplicitIntent handler : handlers){
                    if( fireHandlerWithRuntime(handler,runtimeContext)!= Command.CONTINUE_PROCESSING){
                        break;
                    }
                }
                return runtimeContext.getConvertedResult();
            }
        }
    }

    @Override
    public boolean resume(RuntimeContext runtimeContext) throws Exception {
        return process.execute(runtimeContext);
    }
}
