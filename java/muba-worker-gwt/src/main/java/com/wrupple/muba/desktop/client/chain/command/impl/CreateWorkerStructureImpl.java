package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.CreateWorkerStructure;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Context;

public class CreateWorkerStructureImpl implements CreateWorkerStructure {



    @Override
    public boolean execute(Context ctx) throws Exception {

        ContainerContext context = (ContainerContext) ctx;

        AcceptsOneWidget panel = context.getPanel()

        HelloView helloView = clientFactory.getHelloView();
        helloView.setName(name);
        helloView.setPresenter(this);
        containerWidget.setWidget(helloView.asWidget());


        DesktopPlace place = (DesktopPlace) pc.getWhere();
        if (!dm.isDesktopyConfigured()) {
            if (recoverFromMissconfiguredDesktop(place)) {
                return;
            }

        }

        JavaScriptObject o = dm.getApplicationItem(place);

        JsApplicationItem applicationItem;
        if (o == null) {
            applicationItem = null;
        } else {
            applicationItem = o.cast();
        }
        getActivityProcess(place, applicationItem, new SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));


        return CONTINUE_PROCESSING;
    }
}
