package com.wrupple.muba.event;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import org.apache.commons.chain.Context;

import javax.transaction.UserTransaction;

public interface EventBus
		/* basic implementation: JavaEventBus */ extends Context {

    OutputStream getOutput();
    InputStream getInput();

	PrintWriter getOutputWriter();

    EventRegistry getIntentInterpret();

    boolean fireHandler(ExplicitIntent event, SessionContext session) throws Exception;

    <T> T fireEvent(Intent implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion) throws Exception;

	public UserTransaction getTransaction();

    boolean resume(RuntimeContextImpl runtimeContext) throws Exception;

}