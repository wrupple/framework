package com.wrupple.muba;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class MubaTest extends EasyMockSupport {


	protected Logger log = LogManager.getLogger(MubaTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	protected Injector injector;

	protected RuntimeContext runtimeContext;



	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices( injector.getInstance(ServiceBus.class));

	}
	
	protected abstract void registerServices(ServiceBus switchs);
	
	protected abstract void setUp() throws Exception;



}
