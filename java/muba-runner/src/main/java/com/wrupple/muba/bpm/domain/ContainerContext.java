package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

public interface ContainerContext extends ServiceContext {

    ApplicationState getState();

}
