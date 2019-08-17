package com.wrupple.muba;

import javax.validation.Validator;

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
import com.wrupple.muba.event.server.service.ValidationGroupProvider;

public abstract class AbstractTest extends EasyMockSupport {



	protected Logger log = LogManager.getLogger(AbstractTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	protected Injector injector;


	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices(injector.getInstance(Validator.class), injector.getInstance(ValidationGroupProvider.class), injector.getInstance(ServiceBus.class));

	}
	
	protected abstract void registerServices(Validator v, ValidationGroupProvider g,ServiceBus switchs);



}
