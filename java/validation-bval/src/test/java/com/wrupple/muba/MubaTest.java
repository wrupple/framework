package com.wrupple.muba;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.event.EventBus;

public abstract class MubaTest extends EasyMockSupport {

	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	protected Logger log = LoggerFactory.getLogger(MubaTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	protected Injector injector;

	protected RuntimeContext runtimeContext;


	protected EventSuscriptionMapper mockSuscriptor;


	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices( injector.getInstance(EventBus.class));

	}
	
	protected abstract void registerServices(EventBus switchs);
	
	protected abstract void setUp() throws Exception;


}
