package com.wrupple.muba.cms.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.muba.cms.server.chain.command.WritePublicTimelineEventDiscriminator;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

@Singleton
public class WritePublicTimelineEventDiscriminatorImpl extends CatalogCommand implements WritePublicTimelineEventDiscriminator {

	@Inject
	public WritePublicTimelineEventDiscriminatorImpl(CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	@Override
	public boolean execute(Context context) throws Exception {
		CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
		PersistentCatalogEntity node = (PersistentCatalogEntity) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		Long discriminator = catalog.getId();
		node.setPropertyValue(node.getId(), "catalogEntry");
		node.setPropertyValue(discriminator, "discriminator");
		CatalogDataAccessObject dao = getOrAssembleDataSource(catalog, (CatalogExcecutionContext) context, null);
		unwrap(dao).update(node, node);
		return CONTINUE_PROCESSING;
	}

}
