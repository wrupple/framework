package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.PublicNamespace;
import com.wrupple.muba.catalogs.server.chain.command.KnownHostsProvider;
import com.wrupple.muba.catalogs.server.chain.command.SystemPersonalitiesStorage;
import com.wrupple.muba.catalogs.server.chain.command.impl.MonoliticLocal;
import com.wrupple.muba.catalogs.server.chain.command.impl.SingleSystemPersonalityStorageImpl;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.impl.CatalogResultCacheImpl;
import com.wrupple.muba.catalogs.server.service.impl.NonOperativeCatalogReaderInterceptor;
import com.wrupple.muba.event.chain.impl.ImplicitSuscriptionMapper;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.HostImpl;
import com.wrupple.muba.event.domain.impl.PersonImpl;
import com.wrupple.muba.event.domain.impl.SessionImpl;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.event.server.service.impl.LargeStringFieldDataAccessObjectImpl;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;
import javax.inject.Named;

public class SingleUserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("catalog.storage.people")).toInstance(Person.CATALOG);
		bind(String.class).annotatedWith(Names.named("catalog.storage.peers")).toInstance(Host.CATALOG);

		bind(SystemPersonalitiesStorage.class).to(SingleSystemPersonalityStorageImpl.class);
		bind(KnownHostsProvider.class).to(MonoliticLocal.class);

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
    @Named(SessionContext.SYSTEM)
    public SessionContext sessionContext(@Named(SessionContext.SYSTEM) Session stakeHolderValue) {

        return new SessionContextImpl(stakeHolderValue);
    }

	@Provides
	@Inject
	@javax.inject.Singleton
    @Named(SessionContext.SYSTEM)
    public Session session(@Named(SessionContext.SYSTEM) Person stakeHolderValue) {
		//http://stackoverflow.com/questions/4796172/is-there-a-way-to-get-users-uid-on-linux-machine-using-java
        SessionImpl sessionValue = new SessionImpl();
        sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
		sessionValue.setId(CatalogEntry.PUBLIC_ID);
		sessionValue.setStakeHolderValue(stakeHolderValue);
		return sessionValue;
	}


	@Provides
	@Inject
	@javax.inject.Singleton
	@Named(SessionContext.SYSTEM)
	public Person user() {
		PersonImpl sessionValue = new PersonImpl();
		sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
		sessionValue.setId(CatalogEntry.PUBLIC_ID);
		return sessionValue;
	}

    @Provides
    @Inject
    @javax.inject.Singleton
    @Named(SessionContext.SYSTEM)
    public Host session() {
        HostImpl host = new HostImpl();
        host.setDomain(CatalogEntry.PUBLIC_ID);
        host.setId(CatalogEntry.PUBLIC_ID);
        return host;
    }
}