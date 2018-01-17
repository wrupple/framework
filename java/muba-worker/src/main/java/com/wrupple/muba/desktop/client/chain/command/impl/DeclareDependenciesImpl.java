package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.name.Named;
import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import org.apache.commons.chain.Context;

public class DeclareDependenciesImpl implements DeclareDependencies {

    @Named("muba css")
    String mubaCSS,
    @Named("gae channel api")
    String channelApi,
    @Named("wrupple code")
    String wruppleJs,
    @Named("raphael code")
    String raphaelJs

    @Override
    public boolean execute(Context context) throws Exception {


        System.err.println("will now read styles");
        context.setStaticDesktopCssURI(new String[]{this.mubaCSS});
        context.setStaticDesktopJavaScriptURI(new String[]{this.raphaelJs, this.wruppleJs, this.channelApi});


        return CONTINUE_PROCESSING;
    }
}
