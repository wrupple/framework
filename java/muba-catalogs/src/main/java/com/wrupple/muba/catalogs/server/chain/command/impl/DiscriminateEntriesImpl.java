package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogKey;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

@Singleton
public class DiscriminateEntriesImpl implements CatalogReadTransaction {
	static public final String DISCRIMINATING_FIELD_KEY = "discriminatingField";

	protected static final Logger log = LoggerFactory.getLogger(DiscriminateEntriesImpl.class);
	@Inject
	public DiscriminateEntriesImpl() {
	}

	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		List<CatalogEntry> discriminators = context.getResults();
		int size = discriminators.size();
		List<Object> entryIds = new ArrayList<Object>(discriminators.size());
		for (CatalogKey o : discriminators) {
			entryIds.add(o.getId());
		}
		FilterCriteria discriminatingCriteria = FilterDataUtils.newFilterCriteria();
		String discriminatingField = (String) context.get(DISCRIMINATING_FIELD_KEY);
		if (discriminatingField == null) {
			discriminatingField = HasEntryId.ENTRY_ID_FIELD;
		}
		discriminatingCriteria.pushToPath(discriminatingField);
		discriminatingCriteria.setOperator(FilterData.EQUALS);
		discriminatingCriteria.setValue(entryIds);

		context.getCatalogManager().getRead().execute(context);

		// entries with the right catalog and locale, with all disciminators
		// mixed in
		List<CatalogEntry> members = context.getResults();
		List<CatalogEntry> discriminated = new ArrayList<CatalogEntry>(size);

		HashMap<Long, CatalogEntry> discriminatedMap = new HashMap<Long, CatalogEntry>(size);

		Long disciminator;
		Session session = null;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		FieldDescriptor field = catalog.getFieldDescriptor(discriminatingField);
		log.trace("[BUILD DISCRIMINATOR MAP]");
		for (CatalogEntry e : members) {
			if (session == null) {
				session = context.getCatalogManager().newSession(e);
			}
			disciminator = (Long) context.getCatalogManager().getPropertyValue(catalog, field, e, null, session);
			discriminatedMap.put(disciminator, e);
		}
		// IN THE SAME ORDER AS DISCRIMINATORS, this only works for long primary
		// keys, as you might imagine
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
