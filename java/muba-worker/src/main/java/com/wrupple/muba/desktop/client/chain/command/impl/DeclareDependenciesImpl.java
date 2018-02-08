package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import org.apache.commons.chain.Context;

public class DeclareDependenciesImpl implements DeclareDependencies {



    @Override
    public boolean execute(DesktopRequestContext context) throws Exception {

        // TODO ADD WORKER WIDE DEPENDENCIES?


    /*
    @Named("muba css")
    String mubaCSS,
    @Named("gae channel api")
    String channelApi,
    @Named("wrupple code")
    String wruppleJs,
    @Named("raphael code")
    String raphaelJs

            */

        return CONTINUE_PROCESSING;
    }
}
