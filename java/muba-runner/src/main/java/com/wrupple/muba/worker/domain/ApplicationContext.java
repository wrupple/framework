package com.wrupple.muba.worker.domain;


import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ServiceContext {
    String CATALOG = "ApplicationContext";

    ApplicationContext setRuntimeContext(RuntimeContext requestContext);

    ApplicationState getStateValue();

    void setStateValue(ApplicationState state);


    void setName(String command);
}