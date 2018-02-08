package com.wrupple.muba.desktop.client.widgets;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;

public interface TaskContainer extends Interactive /*Accepts One Widget*/ {
    /**
     * @param callback a transition to call when (if) the user exits modal nested state
     * @return output feature where to present user interaction. If parent is not null, an output feature  modal to the parent will be returned.
     */
    TaskContainer spawnChild(StateTransition<ApplicationContext> callback);

    void setProcessName(String processName, String oldReplacedProcessName);

    /**
     * @return the object where all User interaction widgets ( main, and auxiliary, toolbars, etc.) are registered
     */
    TaskWindow getTaskContent();

    /**
     * @param panel this must be a child of whatever widget was attached by setWidget
     */
    void setTaskContent(TaskWindow panel);

    StateTransition<ApplicationContext> getUserInteractionTaskCallback();

    void setUserInteractionTaskCallback(StateTransition<ApplicationContext> onDone);


}