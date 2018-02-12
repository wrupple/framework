package com.wrupple.muba.worker.shared.widgets;

import com.wrupple.muba.event.domain.TaskToolbarDescriptor;
import com.wrupple.muba.worker.domain.HumanApplicationContext;

public interface Toolbar extends HumanTaskProcessor<Object>/*,HasResizeHandlers*/ {

    void initialize(TaskToolbarDescriptor toolbarDescriptor,
                    TaskToolbarDescriptor parameter,
                    HumanApplicationContext contextParameters);

    void setType(String s);
}
