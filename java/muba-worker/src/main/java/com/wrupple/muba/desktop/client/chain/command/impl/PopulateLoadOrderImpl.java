package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import org.apache.commons.chain.Context;

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
    public boolean execute(Context c) throws Exception {

        DesktopRequestContext context = (DesktopRequestContext) c;


        //set default desktop title (can and should be changed later on)
        context.getWorkerOrderValue().setName(desktopTitle);
        context.getWorkerOrderValue().setCharacterEncoding(characterEncoding);

        String rootActivity = context.getWorkerOrderValue().getHomeActivity();
        if (rootActivity == null || rootActivity.isEmpty()) {
            rootActivity = defaultActivity;
        }
        context.getWorkerOrderValue().setHomeActivity(rootActivity);


        return CONTINUE_PROCESSING;
    }
}
