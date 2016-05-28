package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.chain.command.CatalogCreateTransaction;

@Singleton
public class CatalogCreateTransactionImpl extends CatalogCommand implements CatalogCreateTransaction {

	@Inject
	public CatalogCreateTransactionImpl(CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	@Override
	public boolean execute(Context cxt) throws Exception {
		log.trace("[CREATE START]");
		CatalogExcecutionContext context = (CatalogExcecutionContext) cxt;

		// must have been deserialized by this point
		CatalogEntry result = (CatalogEntry) context.getEntryValue();
		assert result != null : "no data";

		CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(context.getCatalogDescriptor(), context, CatalogEntry.class);

		result = dao.create(result);

		context.addResult(result);
		log.debug("[END] created: {}", result);
		return CONTINUE_PROCESSING;
	}

}
