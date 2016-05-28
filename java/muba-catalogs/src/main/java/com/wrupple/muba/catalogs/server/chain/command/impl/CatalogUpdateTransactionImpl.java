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
import com.wrupple.vegetate.server.chain.command.CatalogUpdateTransaction;
@Singleton
public class CatalogUpdateTransactionImpl  extends CatalogCommand  implements CatalogUpdateTransaction {
	@Inject
	public CatalogUpdateTransactionImpl(CatalogQueryRewriter queryRewriter,
			Provider<CatalogResultCache> cacheProvider,  Provider<CatalogActionTriggerHandler> trigererProvider,
			CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}
	
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		Object targetEntryId = context.getEntry();

		CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(context.getCatalogDescriptor(), context,CatalogEntry.class);
		CatalogEntry originalEntry = dao.read(targetEntryId);
		if(originalEntry==null){
			throw new IllegalArgumentException("no entry found : "+targetEntryId+"@"+context.getCatalog()+"/"+context.getDomain());
		}
		CatalogEntry updatedEntry = (CatalogEntry) context.getEntryValue();
		originalEntry = dao.update( originalEntry, updatedEntry);
		context.addResult(originalEntry);
		log.trace("[UPDATED] {}",originalEntry);
		return CONTINUE_PROCESSING;
	}


}
