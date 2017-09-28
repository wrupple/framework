package com.wrupple.muba.event.server.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.wrupple.muba.event.server.service.FilterNativeInterface;
import com.wrupple.muba.event.server.service.IntrospectionStrategy;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ContextBase;

@Singleton
public class JavaEventBus extends ContextBase implements EventBus {

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

    @Inject
	public JavaEventBus(EventRegistry intentInterpret, EventDispatcher process, @Named("System.out") OutputStream out, @Named("System.in") InputStream in, @Named("event.parallel") Boolean parallel, Provider<UserTransaction> transactionProvider, FilterNativeInterface filterer, @Named("event.sentence") FieldDescriptor handleFieldDescriptor, IntrospectionStrategy instrospector) {
		super();
		this.parallel=parallel;
		this.process=process;
		this.out=out;
		this.in = in;
		this.outputWriter = new PrintWriter(out);
        this.intentInterpret = intentInterpret;
        this.transactionProvider=transactionProvider;
        this.filterer = filterer;

        this.handleField= new CatalogDescriptorImpl(ExplicitIntent.CATALOG,ExplicitIntent.class,-1,ExplicitIntent.CATALOG,null,handleFieldDescriptor);
        this.instrospector = instrospector;

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
    public boolean fireHandler(ExplicitIntent event, SessionContext session) throws Exception {
        RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,null);
        return fireHandlerWithRuntime(event,runtimeContext);
    }

    private boolean fireHandlerWithRuntime(ExplicitIntent event, RuntimeContext runtimeContext) throws Exception {
        runtimeContext.setSentence(event.getSentence());
        runtimeContext.setServiceContract(event.getState());
        boolean regreso = resume(runtimeContext);
        event.setResult(runtimeContext.getResult());
        return regreso;
    }

    @Override
    public <T> T fireEvent(Intent implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion) throws Exception {
        List<ExplicitIntent> handlers = getIntentInterpret().resolveHandlers(implicitRequestContract.getCatalogType());

        if(handlerCriterion!=null && !handlerCriterion.isEmpty()){
            Stream<ExplicitIntent> stream = handlers.stream();
            Instrospection introspector=instrospector.newSession(stream.findAny().get());
            handlers= stream.
                    filter(handler ->filterer.matchAgainstFilters(handler, handlerCriterion, handleField, introspector) ).
                    collect(Collectors.toList());
        }



        if(handlers.size()==1){
            ExplicitIntent call = handlers.get(0);
            fireHandler(call,session);
            return call.getConvertedResult();
        }else if(parallel){
            List<Object> results=  handlers.parallelStream().map(handler -> {
                try {
                    fireHandler(handler,session);
                } catch (Exception e) {
                    handler.setError(e);
                    //implicitRequestContract.addError(e);
                    return handler;
                }
                return handler.getConvertedResult();
            }).collect(Collectors.toList());
            return (T) results;
        }else{
            RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,null);
            for(ExplicitIntent handler : handlers){
                if( fireHandlerWithRuntime(handler,runtimeContext)!= Command.CONTINUE_PROCESSING){
                    break;
                }
            }
            return runtimeContext.getConvertedResult();
        }

    }

    @Override
    public boolean resume(RuntimeContext runtimeContext) throws Exception {
        return process.execute(runtimeContext);
    }
}
