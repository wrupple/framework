package com.wrupple.muba.worker.shared.domain;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;

public interface HumanApplicationContext extends ApplicationContext{

    String ACTION_DISCRIMINATOR = "transaction";

    void setCallback(StateTransition<ApplicationContext> callback);

    StateTransition<ApplicationContext> getCallback();

    HumanTaskProcessor<Object> getUserInteractionInterface();
}
