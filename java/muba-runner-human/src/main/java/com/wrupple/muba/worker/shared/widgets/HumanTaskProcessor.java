package com.wrupple.muba.worker.shared.widgets;

import com.wrupple.muba.desktop.client.widgets.Interactive;
import com.wrupple.muba.desktop.client.widgets.TaskProcessor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;
import com.wrupple.muba.worker.shared.event.HandlesCatalogEvents;

public interface HumanTaskProcessor<T> extends TaskProcessor<T>, HandlesCatalogEvents/*,HasValueChangeHandlers<R> */, Interactive {
    /**
     * configuration framework alters properties then calls this to
     * apply alterations (just to notify)
     * <p>
     * in general this means a complete reset of the widget, but in some
     * cases real-timish alteration of the UI may be an option
     *
     * @param properties
     * @param contextParamenters
     */
    void applyAlterations(ReconfigurationBroadcastEvent properties, ApplicationContext contextParamenters);
}