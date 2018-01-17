package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import org.apache.commons.chain.Context;

public class PopulateLoadOrderImpl implements PopulateLoadOrder {

    @Named("desktop.defaultActivity")
    String defaultActivity,
    @Named("Desktop Title")
    String desktopTitle,
    @Named("Charset")
    String characterEncoding,

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
