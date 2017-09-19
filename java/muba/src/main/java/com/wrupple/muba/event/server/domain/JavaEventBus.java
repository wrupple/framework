package com.wrupple.muba.event.server.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.service.EventRegistry;
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
    private UserTransaction transaction;

    @Inject
	public JavaEventBus(EventRegistry intentInterpret, EventDispatcher process, @Named("System.out") OutputStream out, @Named("System.in") InputStream in, Provider<UserTransaction> transactionProvider) {
		super();
		this.process=process;
		this.out=out;
		this.in = in;
		this.outputWriter = new PrintWriter(out);
        this.intentInterpret = intentInterpret;
        this.transactionProvider=transactionProvider;
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
        runtimeContext.setSentence(event.getSentence());
        runtimeContext.setServiceContract(event.getState());
        boolean regreso = resume(runtimeContext);
        event.setResult(runtimeContext.getResult());
        return regreso;
    }

    @Override
    public <T> T fireEvent(Intent implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion) throws Exception {
        //FIXME use FilterNativeInterface to filter explicit handlers, return compund result (list?) if many handlers match
        List<ExplicitIntent> handlers = getIntentInterpret().resolveHandlers(implicitRequestContract.getCatalogType());
        handlers.
        ExplicitIntent call = null;
        return call.getConvertedResult();
    }





    @Override
    public boolean resume(RuntimeContextImpl runtimeContext) throws Exception {
        return process.execute(runtimeContext);
    }
}
