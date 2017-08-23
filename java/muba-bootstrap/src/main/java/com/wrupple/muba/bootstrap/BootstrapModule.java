package com.wrupple.muba.bootstrap;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.bootstrap.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.bootstrap.server.domain.JavaSystemContext;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.SystemContext;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.RootServiceManifestImpl;
import com.wrupple.muba.bootstrap.server.chain.command.EventDispatcher;
import com.wrupple.muba.bootstrap.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.bootstrap.server.domain.RuntimeContextImpl;
import com.wrupple.muba.bootstrap.server.service.SentenceValidator;
import com.wrupple.muba.bootstrap.server.service.impl.SentenceValidatorImpl;

public class BootstrapModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(SystemContext.class).to(JavaSystemContext.class);//ServletContext
		bind(RuntimeContext.class).to(RuntimeContextImpl.class);//request scoped
		
		bind(String.class).annotatedWith(Names.named("chain.unknownService")).toInstance("chain.unknownService");
		/*
		 * Commands
		 */
		bind(EventDispatcher.class).to(EventDispatcherImpl.class);
		bind(SentenceNativeInterface.class).to(JavaSentenceNativeInterface.class);
		/*bind(EventRegistry.class).to(EventRegistryImpl.class);
		 * Services
		 */
		bind(CatalogFactory.class).toInstance(CatalogFactory.getInstance());
		bind(RootServiceManifest.class).to(RootServiceManifestImpl.class);
		bind(SentenceValidator.class).to(SentenceValidatorImpl.class);
	}

}
