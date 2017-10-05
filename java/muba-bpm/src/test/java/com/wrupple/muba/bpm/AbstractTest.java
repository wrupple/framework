package com.wrupple.muba.bpm;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends EasyMockSupport {


	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	protected Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	protected Injector injector;



	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices( injector.getInstance(EventBus.class));

	}
	
	protected abstract void registerServices(EventBus switchs);



}
