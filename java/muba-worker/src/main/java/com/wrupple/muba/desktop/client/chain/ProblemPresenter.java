package com.wrupple.muba.desktop.client.chain;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;

public interface ProblemPresenter {


    void delegate(ApplicationContext context, StateTransition<ApplicationContext> callback);
}
