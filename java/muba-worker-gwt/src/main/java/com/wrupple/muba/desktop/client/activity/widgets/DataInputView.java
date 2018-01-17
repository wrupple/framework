package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.TakesValue;
import com.wrupple.muba.desktop.domain.HasUserActions;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;

public interface DataInputView<O> extends HumanTask<O, O>, TakesValue<O>,HasUserActions {

}
