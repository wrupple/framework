package com.wrupple.muba.bpm.client.activity.process.state;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.wrupple.vegetate.domain.structure.TreeNode;

public interface StateTransition<T> extends ScheduledCommand, TreeNode<StateTransition<T>> {

    void setResult(T previousStateOutput);

    StateTransition<T> hook(StateTransition<T> hooked);

    void setResultAndFinish(T result);
}
