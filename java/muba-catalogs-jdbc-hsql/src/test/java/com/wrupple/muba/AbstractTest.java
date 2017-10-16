package com.wrupple.muba;

import javax.validation.Validator;

import com.wrupple.muba.event.domain.RuntimeContext;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;

public abstract class AbstractTest extends EasyMockSupport {


	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
	}

	protected Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	protected Injector injector;

	protected RuntimeContext runtimeContext;



	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices(injector.getInstance(Validator.class), injector.getInstance(ValidationGroupProvider.class), injector.getInstance(EventBus.class));

	}
	
	protected abstract void registerServices(Validator v, ValidationGroupProvider g,EventBus switchs);



}
