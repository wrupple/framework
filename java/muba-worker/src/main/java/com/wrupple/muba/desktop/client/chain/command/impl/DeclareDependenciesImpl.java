package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;

public class DeclareDependenciesImpl implements DeclareDependencies {



    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {

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
