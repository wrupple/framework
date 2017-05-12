package com.wrupple.muba.bootstrap;

import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.bootstrap.server.chain.command.impl.JavaSentenceNativeInterface;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.RootServiceManifestImpl;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.chain.command.ContextSwitchCommand;
import com.wrupple.muba.bootstrap.server.chain.command.impl.ContextSwitchCommandImpl;
import com.wrupple.muba.bootstrap.server.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.server.domain.LocalSystemContext;
import com.wrupple.muba.bootstrap.server.service.SentenceValidator;
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
		bind(ContextSwitchCommand.class).to(ContextSwitchCommandImpl.class);
		bind(SentenceNativeInterface.class).to(JavaSentenceNativeInterface.class);
		/*
		 * Services
		 */
		bind(CatalogFactory.class).toInstance(CatalogFactory.getInstance());
		bind(RootServiceManifest.class).to(RootServiceManifestImpl.class);
		bind(SentenceValidator.class).to(SentenceValidatorImpl.class);
	}

}
