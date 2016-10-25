package com.wrupple.muba.catalogs;

import java.io.PrintWriter;

import javax.inject.Provider;
import javax.transaction.UserTransaction;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldConstraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.LocalizedString;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.domain.PublicNamespace;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.server.service.impl.CatalogResultCacheImpl;
import com.wrupple.muba.catalogs.server.service.impl.LargeStringFieldDataAccessObjectImpl;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;

public class SingleUserModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
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
	@Inject
	public ExcecutionContext excecutionContext(SessionContext session,
			Provider<UserTransaction> transactionProvider) {
		return new ExcecutionContextImpl(new PrintWriter(System.out), session, transactionProvider);
	}
	
	@Provides
	public CatalogNamespace publicNamespace() {
		return new PublicNamespace();
	}
}