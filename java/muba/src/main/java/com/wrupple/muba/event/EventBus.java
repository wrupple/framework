package com.wrupple.muba.event;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

public interface EventBus
		/* basic implementation: EventBusImpl */ extends Context {

    OutputStream getOutput();
    InputStream getInput();

	PrintWriter getOutputWriter();

    EventRegistry getIntentInterpret();

    void broadcastEvent(Event event, CatalogDescriptor catalogDescriptor, RuntimeContext runtimeContext, List<FilterCriteria> explicitlySuscriptedObservers) throws Exception;

    boolean fireHandler(Intent event, SessionContext session) throws Exception;

    <T> T fireEvent(Event implicitRequestContract, RuntimeContext parent, List<FilterCriteria> handlerCriterion) throws Exception;

    <T> T fireEvent(Event implicitRequestContract, SessionContext session, List<FilterCriteria> handlerCriterion) throws Exception;

    boolean resume(RuntimeContext runtimeContext) throws Exception;

    boolean hasInterpret(String next);

    NaturalLanguageInterpret getInterpret(String next);

    void registerInterpret(String constraint, NaturalLanguageInterpret constraintInterpret);
}