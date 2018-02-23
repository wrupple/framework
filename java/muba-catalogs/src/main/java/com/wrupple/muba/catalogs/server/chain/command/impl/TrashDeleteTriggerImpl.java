package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;

@Singleton
public class TrashDeleteTriggerImpl implements TrashDeleteTrigger {
	private static final Logger log = LoggerFactory.getLogger(TrashDeleteTriggerImpl.class);


	@Inject
	public TrashDeleteTriggerImpl() {
		super();
	}

	// when trash items are deleted
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		List<Trash> trash = (List) context.getResults();
		if (trash == null) {
			trash = (List) context.getOldValues();
			context.setResults(trash);
		}

		if (trash != null) {
			log.warn("[PERMANENTLY DELETING DUMPED TRASH ITEMS]");
			String catalogId = null;
			List<Object> ids = new ArrayList<Object>();
			for (Trash entry : trash) {

				if (catalogId == null || !catalogId.equals(entry.getCatalog())) {
					// ONLY CHANGE SERVICES WHEN TRASH TYPE CHANGES, HOPEFULLY
					// OPTIMIZING THE PROCESS SINCE TRASH ITEMS ARE READ IN
					// ORDER

					
					if (ids.isEmpty()) {
					} else {
						flush(ids, catalogId, context);
						ids.clear();
					}
					catalogId = entry.getCatalog();

				}
				ids.add(entry.getId());
			}
		}

		return CONTINUE_PROCESSING;
	}

	private void flush(List<Object> ids, String catalogId, CatalogActionContext context) throws Exception {
		log.trace("delete consecutive trash items so far");
		context.triggerDelete(catalogId,FilterDataUtils.createSingleKeyFieldFilter(CatalogEntry.ID_FIELD, ids),null);
	}

}
