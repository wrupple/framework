package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.server.domain.FilterDataOrderingImpl;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class TrashDeleteTriggerImpl extends CatalogCommand implements TrashDeleteTrigger {
	private static final Logger log = LoggerFactory.getLogger(TrashDeleteTriggerImpl.class);
	@Inject
	public TrashDeleteTriggerImpl(CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider, Provider<CatalogActionTriggerHandler> trigererProvider,
			CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		Trash e = (Trash) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		if (e == null) {
			e = (Trash) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		}
		if (e == null) {
			log.warn("[EMPTY TRASH ITEMS]");
			CatalogDataAccessObject<Trash> trashDao = getOrAssembleDataSource(database.getDescriptorForName(Trash.CATALOG, context), context, Trash.class);
			trashDao = unwrap(trashDao);
			FilterData all = FilterDataUtils.newFilterData();
			all.setConstrained(false);
			all.addOrdering(new FilterDataOrderingImpl(HasCatalogId.FIELD, false));
			List<Trash> trash = trashDao.read(all);
			
			String catalogId = null;
			CatalogDescriptor descriptor = null;
			CatalogDataAccessObject<CatalogEntry> dao = null;
			
			context.addResuls((List)trash);
			trashDao.beginTransaction();
			for(Trash entry : trash){
				if (catalogId == null || !catalogId.equals(entry.getCatalogId())) {
					// ONLY CHANGE SERVICES WHEN TRASH TYPE CHANGES, HOPEFULLY
					// OPTIMIZING THE PROCESS SINCE TRASH ITEMS ARE READ IN
					// ORDER
					catalogId = entry.getCatalogId();
					descriptor = database.getDescriptorForName(catalogId, context);
					dao = getOrAssembleDataSource(descriptor, context, CatalogEntry.class);
					dao.beginTransaction();
				}else{
					dao.commitTransaction();
				}
				dao.delete(dao.read(entry.getCatalogEntryId()));
				trashDao.delete(entry);
			}
			dao.commitTransaction();
			trashDao.commitTransaction();
		} else {
			log.trace("[PERMANENTLY DELETE TRASH ITEM] {}",e);
			String catalogId = e.getCatalogId();
			Object entryId = e.getCatalogEntryId();
			CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(database.getDescriptorForName(catalogId, context), context, CatalogEntry.class);
			CatalogEntry o = dao.read(entryId);
			if (o != null) {
				dao.delete(o);
			}
			context.addResult(o);
		}

		return CONTINUE_PROCESSING;
	}

}
