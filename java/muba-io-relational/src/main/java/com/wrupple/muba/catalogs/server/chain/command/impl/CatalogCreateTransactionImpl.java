package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.domain.CacheInvalidationEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.EntryCreators;

@Singleton
public class CatalogCreateTransactionImpl implements CatalogCreateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogCreateTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;

	
	private final EntryCreators creators;
	
	
	
	@Inject
	public CatalogCreateTransactionImpl(EntryCreators creators,CatalogActionTriggerHandler trigerer,CatalogFactory factory, String creatorsDictionary) {
		this.trigerer=trigerer;
		this.creators=creators;
	}

	
	@Override
	public boolean execute(Context cxt) throws Exception {
		log.trace("[CREATE START]");
		CatalogActionContext context = (CatalogActionContext) cxt;

		// must have been deserialized by this point
		CatalogEntry result = (CatalogEntry) context.getEntryValue();
		assert result != null : "no data";
		context.setAction(CatalogActionRequest.CREATE_ACTION);
		trigerer.execute(context);
		
		CatalogDescriptor catalog=context.getCatalogDescriptor();
		
		DataCreationCommand createDao = (DataCreationCommand) creators.getCommand(String.valueOf(catalog.getStorage()));
		createDao.execute(context);
		
		CatalogEntry regreso = context.getResult();
		
		if(regreso!=null){
			context.getTransactionHistory().didCreate(context, regreso, createDao);
		}
		
		//local cache
		CatalogResultCache cache = context.getCatalogManager().getCache(catalog, context);
		if (cache != null) {
			cache.put(context, catalog.getCatalog(), regreso);
			cache.clearLists(context,catalog.getCatalog());
		}
		//cache invalidation
		context.getCatalogManager().addBroadcastable(new CacheInvalidationEventImpl(context.getDomain(), catalog.getCatalog(), CatalogActionRequest.CREATE_ACTION, regreso), context);
		
		
		trigerer.postprocess(context, context.getError());
		
		context.setResults(Collections.singletonList(result));
		log.trace("[END] created: {}", result);
		return CONTINUE_PROCESSING;
	}

	

}
