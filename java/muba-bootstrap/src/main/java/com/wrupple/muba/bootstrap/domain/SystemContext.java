package com.wrupple.muba.bootstrap.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.wrupple.muba.bootstrap.server.domain.RuntimeContextImpl;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

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