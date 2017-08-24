package com.wrupple.muba.event.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.wrupple.muba.event.server.domain.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import org.apache.commons.chain.Context;

import javax.transaction.UserTransaction;

public interface SystemContext
		/* basic implementation: JavaSystemContext */ extends Context {

    OutputStream getOutput();
    InputStream getInput();

	PrintWriter getOutputWriter();

    EventRegistry getIntentInterpret();

    boolean fireEvent(UserEvent event, SessionContext session) throws Exception;

	public UserTransaction getTransaction();

    boolean resume(RuntimeContextImpl runtimeContext) throws Exception;
}