package com.wrupple.muba.event;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.event.server.domain.JavaSystemContext;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.impl.EventRegistryImpl;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.SystemContext;
import com.wrupple.muba.event.domain.RootServiceManifest;
import com.wrupple.muba.event.domain.RootServiceManifestImpl;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.event.server.domain.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.SentenceValidator;
import com.wrupple.muba.event.server.service.impl.SentenceValidatorImpl;

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
		bind(EventRegistry.class).to(EventRegistryImpl.class);
		bind(CatalogFactory.class).toInstance(CatalogFactory.getInstance());
		bind(RootServiceManifest.class).to(RootServiceManifestImpl.class);
		bind(SentenceValidator.class).to(SentenceValidatorImpl.class);
	}

}
