package com.wrupple.muba.bootstrap;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;
import com.wrupple.muba.bootstrap.server.chain.command.impl.ServiceInvocationCommandImpl;
import com.wrupple.muba.bootstrap.server.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.server.domain.LocalSystemContext;
import com.wrupple.muba.bootstrap.server.service.SentenceValidator;
import com.wrupple.muba.bootstrap.server.service.impl.BootstrapImpl;
import com.wrupple.muba.bootstrap.server.service.impl.SentenceValidatorImpl;

public class BootstrapModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(ApplicationContext.class).to(LocalSystemContext.class);//ServletContext
		bind(ExcecutionContext.class).to(ExcecutionContextImpl.class);//request scoped
		
		bind(String.class).annotatedWith(Names.named("chain.unknownService")).toInstance("chain.unknownService");
		/*
		 * Commands
		 */
		bind(ServiceInvocationCommand.class).to(ServiceInvocationCommandImpl.class);
		/*
		 * Services
		 */
		bind(Bootstrap.class).to(BootstrapImpl.class);
		bind(SentenceValidator.class).to(SentenceValidatorImpl.class);
	}

}
