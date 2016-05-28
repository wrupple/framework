package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.ResultHandlingService;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class DiscriminateEntries extends CatalogReadTransactionImpl {
	static public final String DISCRIMINATING_FIELD_KEY = "discriminatingField";
	
	@Inject
	public DiscriminateEntries(@Named("catalog.publicDiscriminator") String publicParameter, ResultHandlingService genericSummaryBuilder, CatalogQueryRewriter queryRewriter,
			Provider<CatalogResultCache> cacheProvider, Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor,
			DatabasePlugin daoFactory) {
		super(publicParameter, genericSummaryBuilder, queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		List<CatalogEntry> discriminators = context.getResults();
		int size = discriminators.size();
		List<Object> entryIds = new ArrayList<Object>(discriminators.size());
		for (CatalogKey o : discriminators) {
			entryIds.add(o.getId());
		}
		FilterCriteria discriminatingCriteria = FilterDataUtils.newFilterCriteria();
		String discriminatingField = (String) context.get(DISCRIMINATING_FIELD_KEY);
		if(discriminatingField==null){
			discriminatingField = HasEntryId.FIELD;
		}
		discriminatingCriteria.pushToPath(discriminatingField);
		discriminatingCriteria.setOperator(FilterData.EQUALS);
		discriminatingCriteria.setValue(entryIds);
		
		super.execute(context);

		// entries with the right catalog and locale, with all disciminators
		// mixed in
		List<CatalogEntry> members = context.getResults();
		List<CatalogEntry> discriminated = new ArrayList<CatalogEntry>(size);

		HashMap<Long, CatalogEntry> discriminatedMap = new HashMap<Long, CatalogEntry>(size);

		Long disciminator;
		Session session=null;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		FieldDescriptor field = catalog.getFieldDescriptor(discriminatingField);
		log.trace("[BUILD DISCRIMINATOR MAP]");
		for (CatalogEntry e : members) {
			if(session==null){
				session = accessor.newSession(e);
			}
			disciminator = (Long) accessor.getPropertyValue(catalog, field, e, null, session);
			discriminatedMap.put(disciminator, e);
		}
		// IN THE SAME ORDER AS DISCRIMINATORS, this only works for long primary keys, as you might imagine
		log.debug("[BUILD ORDERED DISCRIMINATEES LIST]");
		CatalogEntry localizedEntity;
		for (CatalogKey o : discriminators) {
			localizedEntity = discriminatedMap.get(o.getId());
			discriminated.add(localizedEntity);
		}
		context.setResults(discriminated);
		return CONTINUE_PROCESSING;
	}
}
