package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.I18nProcessing;

@Singleton
public class SameEntityLocalizationStrategy implements I18nProcessing {


	protected static final Logger log = LoggerFactory.getLogger(SameEntityLocalizationStrategy.class);
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		HasAccesablePropertyValues entry;
		LocalizedEntityWrapper localizedEntry;
		List<CatalogEntry> result = context.getResults();
		int size = result.size();
		List<CatalogEntry> regreso = new ArrayList<CatalogEntry>(size);
		String locale = context.getLocale();
		Long id = context.getCatalogDescriptor().getId();
		log.trace("[WRAPPING RESULTS] {}/{}",size,locale);
		for (int i = 0; i < size; i++) {
			entry = (HasAccesablePropertyValues) result.get(i);

			localizedEntry = new LocalizedEntityWrapper(entry, locale, id);

			regreso.add(localizedEntry);
		}
		context.setResults(regreso);
		return CONTINUE_PROCESSING;
	}

}
