package com.wrupple.muba.catalogs;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.PublicNamespace;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.server.service.impl.CatalogResultCacheImpl;
import com.wrupple.muba.catalogs.server.service.impl.LargeStringFieldDataAccessObjectImpl;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;

public class SingleUserModule extends AbstractModule {

	@Override
	protected void configure() {
		
		/*
		 * CONFIGURATION
		 */
		// used when building urls for outsiders
		
		bind(Boolean.class).annotatedWith(Names.named("system.multitenant")).toInstance(false);

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
}