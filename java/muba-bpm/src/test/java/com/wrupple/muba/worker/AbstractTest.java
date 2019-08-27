package com.wrupple.muba.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.wrupple.muba.event.domain.SessionContext.SYSTEM;

public abstract class AbstractTest extends EasyMockSupport {


    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Logger log = LogManager.getLogger(AbstractTest.class);
    protected Injector injector;
    protected ServiceBus wrupple;
    protected SessionContext session;


    public final void init(Module... modules) {
        injector = Guice.createInjector(modules);
        registerServices(injector.getInstance(ServiceBus.class));
        wrupple = injector.getInstance(ServiceBus.class);
        session = injector.getInstance(Key.get(SessionContext.class, Names.named(SYSTEM)));

    }

    protected abstract void registerServices(ServiceBus switchs);


}
