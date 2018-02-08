package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.domain.WorkerRequest;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;

@Singleton
public class PopulateLoadOrderImpl implements PopulateLoadOrder {

    private final String defaultActivity;
    private final String desktopTitle;
    private final String characterEncoding;

    @Inject
    public PopulateLoadOrderImpl(    @Named("worker.defaultActivity")
                                             String defaultActivity,     @Named("worker.intialTitle")
            String desktopTitle,     @Named("worker.charset")
            String characterEncoding) {
        this.defaultActivity = defaultActivity;
        this.desktopTitle = desktopTitle;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {
        WorkerRequest request = (WorkerRequest) requestContext.getServiceContract();
        WorkerRequestContext context = requestContext.getServiceContext();


        //set default desktop title (can and should be changed later on)
        context.getWorkerState().setName(desktopTitle);
        context.getWorkerState().setCharacterEncoding(characterEncoding);

        String rootActivity = context.getWorkerState().getHomeActivity();
        if (rootActivity == null || rootActivity.isEmpty()) {
            rootActivity = defaultActivity;
        }
        context.getWorkerState().setHomeActivity(rootActivity);


        return CONTINUE_PROCESSING;
    }
}
