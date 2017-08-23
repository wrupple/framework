package com.wrupple.muba.bootstrap.server.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.chain.command.EventDispatcher;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

@Singleton
public class JavaSystemContext extends ContextBase implements SystemContext {

	private static final long serialVersionUID = -7144539787781019055L;

	private final PrintWriter outputWriter;
    private final OutputStream out;
    private final InputStream in;
	// use a registry method, this stays private please stop it!!
	private final EventRegistry intentInterpret;
    private final EventDispatcher process;
	private final Provider<UserTransaction> transactionProvider;
    private UserTransaction transaction;

    @Inject
	public JavaSystemContext(EventDispatcher process, @Named("System.out") OutputStream out, @Named("System.in") InputStream in,EventRegistry intentInterpret,Provider<UserTransaction> transactionProvider) {
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
	public boolean fireEvent(UserEvent event, SessionContext session) throws Exception {
		RuntimeContextImpl runtimeContext = new RuntimeContextImpl(this,session,null);
        runtimeContext.setSentence(event.getSentence());
        runtimeContext.setServiceContract(event.getState());
            boolean regreso = process.execute(runtimeContext);
            event.setResult(runtimeContext.getResult());
            return regreso;
	}

    @Override
    public boolean resume(RuntimeContextImpl runtimeContext) throws Exception {
        return process.execute(runtimeContext);
    }
}
