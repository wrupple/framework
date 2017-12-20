package com.wrupple.muba;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MubaTest extends EasyMockSupport {

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
    }

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Logger log = LoggerFactory.getLogger(MubaTest.class);
    protected Injector serverInjector;
    protected Injector clientInjector;


    protected RuntimeContext runtimeContext;


    protected EventSuscriptionMapper mockSuscriptor;


    public final void init(Module... modules) {
        serverInjector = Guice.createInjector(modules);
        clientInjector = Guice.createInjector(modules);
        registerServices(serverInjector.getInstance(EventBus.class), clientInjector.getInstance(EventBus.class));

    }

    protected abstract void registerServices(EventBus serverBus, EventBus clientBus);

    protected abstract void setUp() throws Exception;


}
