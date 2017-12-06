package com.wrupple.muba.event;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.Incorporate;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import com.wrupple.muba.event.server.chain.command.impl.IncorporateImpl;

public class DispatcherModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BindService.class).to(BindServiceImpl.class);
        bind(Incorporate.class).to(IncorporateImpl.class);
        bind(Dispatch.class).to(DispatchImpl.class);
    }
}
