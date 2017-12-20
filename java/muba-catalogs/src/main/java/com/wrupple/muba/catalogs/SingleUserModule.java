package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.PublicNamespace;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.impl.CatalogResultCacheImpl;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Container;
import com.wrupple.muba.event.domain.ContainerContext;
import com.wrupple.muba.event.domain.impl.ContainerImpl;
import com.wrupple.muba.event.server.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.event.server.service.impl.LargeStringFieldDataAccessObjectImpl;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;
import javax.inject.Named;

public class SingleUserModule extends AbstractModule {

	@Override
	protected void configure() {
		
		/*
		 * CONFIGURATION
		 */
		// used when building urls for outsiders

		bind(Boolean.class).annotatedWith(Names.named("system.multitenant")).toInstance(false);
		bind(Boolean.class).annotatedWith(Names.named("security.anonStakeHolder")).toInstance(true);


		/*
		 * SERVICES
		 */
		bind(Context.class).annotatedWith(Names.named("catalog.cache")).to(ContextBase.class).in(Singleton.class);
		bind(CatalogResultCache.class).to(CatalogResultCacheImpl.class);
		bind(LargeStringFieldDataAccessObject.class).to(LargeStringFieldDataAccessObjectImpl.class);
		bind(CatalogReaderInterceptor.class).to(NonOperativeCatalogReaderInterceptor.class);
		
	}
	
	
	@Provides
	public CatalogNamespace publicNamespace() {
		return new PublicNamespace();
	}

	@Provides
	@Inject
	@javax.inject.Singleton
    @Named(ContainerContext.SYSTEM)
    public ContainerContext sessionContext(@Named(ContainerContext.SYSTEM) Container stakeHolderValue) {


        return new ContainerContextImpl(stakeHolderValue);
    }

	@Provides
	@Inject
	@javax.inject.Singleton
    @Named(ContainerContext.SYSTEM)
    public Container sessionContext() {
        ContainerImpl sessionValue = new ContainerImpl();
        sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
		sessionValue.setId(CatalogEntry.PUBLIC_ID);
		return sessionValue;
	}

}