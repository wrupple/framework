package com.wrupple.muba;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;

public abstract class MubaTest extends EasyMockSupport {

	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	protected Logger log = LoggerFactory.getLogger(MubaTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	protected Bootstrap muba;

	protected Injector injector;

	protected RuntimeContext runtimeContext;



	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		
		muba = injector.getInstance(Bootstrap.class);

	}
	
	protected abstract void setUp() throws Exception;


}
