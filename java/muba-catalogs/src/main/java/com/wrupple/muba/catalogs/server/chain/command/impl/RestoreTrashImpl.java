package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.RestoreTrash;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.server.domain.FilterDataOrderingImpl;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class RestoreTrashImpl extends CatalogCommand implements RestoreTrash {

	@Inject
	public RestoreTrashImpl(CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		Trash e = (Trash) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		if (e == null) {
			log.warn("[RESTORE ALL TRASH ITEMS]");

			Session session = accessor.newSession(null);
			CatalogDataAccessObject<Trash> trashDao = getOrAssembleDataSource(database.getDescriptorForName(Trash.CATALOG, context), context, Trash.class);
			trashDao = unwrap(trashDao);
			FilterData all = FilterDataUtils.newFilterData();
			all.setConstrained(false);
			all.addOrdering(new FilterDataOrderingImpl(HasCatalogId.FIELD, false));

			// READ ALL TRASH ITEMS ORDERED BY CATALOG TYPE
			List<Trash> trash = trashDao.read(all);
			String catalogId = null;
			CatalogDescriptor descriptor = null;
			CatalogDataAccessObject<CatalogEntry> dao = null;
			FieldDescriptor trashField = null;
			
			context.addResuls((List)trash);
			trashDao.beginTransaction();
			for (Trash entry : trash) {
				if (catalogId == null || !catalogId.equals(entry.getCatalogId())) {
					// ONLY CHANGE SERVICES WHEN TRASH TYPE CHANGES, HOPEFULLY
					// OPTIMIZING THE PROCESS SINCE TRASH ITEMS ARE READ IN
					// ORDER
					catalogId = entry.getCatalogId();
					descriptor = database.getDescriptorForName(catalogId, context);
					dao = getOrAssembleDataSource(descriptor, context, CatalogEntry.class);
					trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
					dao.beginTransaction();
				}else{
					dao.commitTransaction();
				}
				super.undelete(e, context, descriptor, trashField, session, trashDao, dao);
			}
			dao.commitTransaction();
			trashDao.commitTransaction();
		} else {

				log.debug("[RESTORE TRASH ITEM] {}", e);

				super.undelete(e, context);

				// SINCE THIS TRIGGER IS PERFORMED BEFORE ACTION IS COMMITED,
				// AND FAILS SILENTLY, then when the restoring action is
				// attempted, it will fail and everything will be fine
				context.addResult(e);
		}

		return CONTINUE_PROCESSING;
	}

}
