package com.wrupple.muba.bpm.domain;


import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ServiceContext {
    final String CATALOG = "ApplicationContext";

    ApplicationContext setRuntimeContext(RuntimeContext requestContext);

    ApplicationState getStateValue();

    void setStateValue(ApplicationState state);




}