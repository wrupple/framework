package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.TakesValue;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.desktop.domain.HasUserActions;

public interface DataInputView<O> extends HumanTask<O, O>, TakesValue<O>,HasUserActions {

}
