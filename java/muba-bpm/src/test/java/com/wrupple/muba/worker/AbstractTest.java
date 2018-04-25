package com.wrupple.muba.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.event.ServiceBus;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class AbstractTest extends EasyMockSupport {


    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Logger log = LogManager.getLogger(AbstractTest.class);
    protected Injector injector;


    public final void init(Module... modules) {
        injector = Guice.createInjector(modules);
        registerServices(injector.getInstance(ServiceBus.class));

    }

    protected abstract void registerServices(ServiceBus switchs);


}
