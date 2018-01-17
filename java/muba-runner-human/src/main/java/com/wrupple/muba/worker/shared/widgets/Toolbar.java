package com.wrupple.muba.worker.shared.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.TaskToolbarDescriptor;

public interface Toolbar extends HumanTaskProcessor<JavaScriptObject, JavaScriptObject>/*,HasResizeHandlers*/ {

    void initialize(TaskToolbarDescriptor toolbarDescriptor,
                    TaskToolbarDescriptor parameter,
                    ApplicationContext contextParameters);

    void setType(String s);
}
