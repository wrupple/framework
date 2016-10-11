package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.domain.CacheInvalidationEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Writers;
@Singleton
public class CatalogUpdateTransactionImpl  implements CatalogUpdateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogUpdateTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;

	
	private final CatalogReadTransaction read;

	private final Writers writers;
	
	
	@Inject
	public CatalogUpdateTransactionImpl(CatalogReadTransaction read,CatalogActionTriggerHandler trigerer, CatalogFactory factory, Writers writers) {
		this.trigerer=trigerer;
		this.read=read;
		this.writers=writers;
	}
	
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		read.execute(context);
		
		context.setOldValue(context.getResult());
		
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		context.setAction(CatalogActionRequest.WRITE_ACTION);
		trigerer.execute(context);
		

		DataWritingCommand dao = (DataWritingCommand) writers.getCommand(String.valueOf(catalog.getStorage()));
		
		dao.execute(context);
		
		context.getTransactionHistory().didUpdate(context, context.getResult(), context.getOldValue(), dao);
		trigerer.postprocess(context, context.getError());
		CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);
		if(cache!=null){
			cache.update(context,catalog.getCatalog(),context.getOldValue(), context.getResult());
		}
		context.getCatalogManager().addBroadcastable(new CacheInvalidationEventImpl(context.getDomain(), context.getCatalog(), CatalogActionRequest.WRITE_ACTION, context.getResult()), context);
		
		log.trace("[UPDATED] {}",context.getResult());
		return CONTINUE_PROCESSING;
	}

}
