package com.wrupple.muba.worker.domain;


import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Command;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ServiceContext {
    String CATALOG = "ApplicationContext";

    ApplicationContext setRuntimeContext(RuntimeContext requestContext, ContainerState container);

    ApplicationState getStateValue();


    ContainerState getContainerStateValue();


    void setStateValue(ApplicationState state);


    void setName(String command);

    Command getStateInstance(Task step);
}