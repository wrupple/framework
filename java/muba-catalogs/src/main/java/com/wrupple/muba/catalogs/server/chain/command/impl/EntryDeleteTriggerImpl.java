package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
@Singleton
public class EntryDeleteTriggerImpl  extends CatalogCommand  implements EntryDeleteTrigger {
	Provider<Trash> trashp;
	
	@Inject
	public EntryDeleteTriggerImpl( Provider<Trash> trashp, CatalogQueryRewriter queryRewriter,
			Provider<CatalogResultCache> cacheProvider,  Provider<CatalogActionTriggerHandler> trigererProvider,
			CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
		this.trashp = trashp;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		CatalogEntry e=(CatalogEntry) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		if(e==null){
			e=(CatalogEntry) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		}
		
		CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
		Session session = accessor.newSession(e);
		FieldDescriptor field = catalog.getFieldDescriptor(Trash.TRASH_FIELD);
		Boolean trashed  = (Boolean) accessor.getPropertyValue(catalog, field, e, null, session);
		if(trashed!= null && trashed){
			Trash trashItem = trashp.get();
			trashItem.setName(e.getName());
			trashItem.setCatalogEntryId(e.getIdAsString());
			trashItem.setCatalogId(catalog.getCatalogId());
			
			CatalogDataAccessObject<Trash> trashDao = getOrAssembleDataSource(database.getDescriptorForName(Trash.CATALOG, (CatalogExcecutionContext) context),(CatalogExcecutionContext) context, Trash.class);
			trashDao.create(trashItem);
		}
		log.trace("[/DELETE TRIGGER]");
		return CONTINUE_PROCESSING;
	}

}
