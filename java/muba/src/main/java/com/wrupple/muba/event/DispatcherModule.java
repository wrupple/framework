package com.wrupple.muba.event;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Run;
import com.wrupple.muba.event.server.chain.command.Incorporate;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.RunImpl;
import com.wrupple.muba.event.server.chain.command.impl.IncorporateImpl;

public class DispatcherModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BindService.class).to(BindServiceImpl.class);
        bind(Incorporate.class).to(IncorporateImpl.class);
        bind(Run.class).to(RunImpl.class);
    }
}
