package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;

@Singleton
public class PopulateLoadOrderImpl implements PopulateLoadOrder {

    private final String desktopTitle;
    private final String characterEncoding;

    @Inject
    public PopulateLoadOrderImpl(     @Named("worker.intialTitle")
            String desktopTitle,     @Named("worker.charset")
            String characterEncoding) {
        this.desktopTitle = desktopTitle;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {
        WorkerContract request = (WorkerContract) requestContext.getServiceContract();
        WorkerRequestContext context = requestContext.getServiceContext();


        //set default desktop title (can and should be changed later on)
        context.getWorkerState().setName(desktopTitle);
        context.getWorkerState().setCharacterEncoding(characterEncoding);

        String rootActivity = context.getWorkerState().getHomeActivity();
        if (rootActivity == null || rootActivity.isEmpty()) {
            rootActivity = request.getRootActivity();
        }
        context.getWorkerState().setHomeActivity(rootActivity);


        return CONTINUE_PROCESSING;
    }
}
