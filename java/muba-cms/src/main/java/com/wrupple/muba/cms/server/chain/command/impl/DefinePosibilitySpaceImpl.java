package com.wrupple.muba.cms.server.chain.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogReadTransactionImpl;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.ResultHandlingService;
import com.wrupple.muba.cms.server.chain.command.DefinePosibilitySpace;
import com.wrupple.muba.cms.server.domain.ContentContext;
import com.wrupple.muba.cms.server.domain.ContentContext.FoundValue;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

@Singleton
public class DefinePosibilitySpaceImpl extends CatalogReadTransactionImpl implements DefinePosibilitySpace {

	
	@Inject
	public DefinePosibilitySpaceImpl( @Named("catalog.publicDiscriminator") String publicParameter, ResultHandlingService genericSummaryBuilder,CatalogQueryRewriter queryRewriter,
			Provider<CatalogResultCache> cacheProvider,  Provider<CatalogActionTriggerHandler> trigererProvider,
			CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(publicParameter, genericSummaryBuilder, queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	@Override
	public boolean execute(Context c) throws Exception {
		ContentContext context= (ContentContext) c;
		FilterData filter = context.getFilter();
		log.trace("[POSIBILITY SPACE CRITERIA] {}",filter);
		FieldDescriptor field = context.getField();
		String catalog = field.getForeignCatalogName();
		CatalogExcecutionContext read = context.getCatalogContext();
		read.setFilter(filter);
		read.setCatalog(catalog);
		read.setEntry(null);
		
		super.execute(read);
		List<CatalogEntry> results = read.getResults();
		
		if(results==null || results.isEmpty()){
			
		}else{
			
			List<FoundValue> foundValues = new ArrayList<ContentContext.FoundValue>(results.size());
			for(CatalogEntry e: results){
				foundValues.add(new FoundValue(e, 0));
			}
			context.setFoundValues(foundValues);
		}
		
		return CONTINUE_PROCESSING;
	}


}
