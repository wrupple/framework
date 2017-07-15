package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

/**
 * 
 * delegates to whatever DAO the LocalizedEntity catalog uses to access data,
 * however, this implementation intercepts the creation of any entity pointing
 * to a catalog that uses
 * {@link com.wrupple.muba.catalogs.server.service.impl.SameEntityLocalizationStrategy}
 * and updates/creates columns on that same entity, but for the given locale
 * 
 * @author japi
 *
 */
@Singleton
public class LocalizedEntityInterceptorDAO implements CatalogCreateTransaction {

	public LocalizedEntityInterceptorDAO() {
		super();
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		DistributiedLocalizedEntry o = (DistributiedLocalizedEntry) context.getEntryValue();

		// check all required data is present
		Long numericCatalogId = o.getCatalog();
		Long entryId = o.getEntry();
		String locale = o.getLocale();
		if (numericCatalogId == null || entryId == null || locale == null) {
			throw new IllegalArgumentException(
					"Attempt to create a localized entity pointing to no existing catalog entry");
		}
		// format locale to be prepended to a field (looks like field_locale)
		locale = "_" + locale;

		CatalogActionContext localize = context.getCatalogManager().spawn(context);

		localize.setEntry(numericCatalogId);
		localize.setFilter(null);
		localize.setCatalog(CatalogDescriptor.CATALOG_ID);
		// what catalog is this localized entity pointing to?
		context.getCatalogManager().getRead().execute(localize);
		CatalogDescriptor pointsTo = localize.getEntryResult();
		Session session = context.getCatalogManager().access().newSession(pointsTo);

		// what strategy does the referenced catalog use to localize it's
		// entities
		int strategy = pointsTo.getLocalization();
		if (0 == strategy /* CONSOLIDATED */) {
			// this is the special case we need to intercept
			String catalogId = pointsTo.getDistinguishedName();
			session.resample(o);

			// read localized field values
			List<String> values = (List<String>) o.getProperties();

			// read localizable entity
			localize.setEntry(entryId);
			localize.setFilter(null);
			localize.setCatalog(catalogId);
			CatalogDescriptor localizedCatalog = localize.getCatalogDescriptor();
			context.getCatalogManager().getRead().execute(localize);
			PersistentCatalogEntity targetEntity = localize.getEntryResult();

			// write localized values
			FieldDescriptor field;
			String fieldId, value;
			int indexOfSeparator;
			for (String rawValue : values) {
				indexOfSeparator = rawValue.indexOf('=');
				fieldId = rawValue.substring(0, indexOfSeparator);
				value = rawValue.substring(indexOfSeparator + 1);
				field = localizedCatalog.getFieldDescriptor(fieldId);
				if (field != null && field.isWriteable() && field.getDataType() == CatalogEntry.STRING_DATA_TYPE) {

					targetEntity.setPropertyValue(value, new StringBuilder(fieldId.length() + locale.length())
							.append(fieldId).append(locale).toString());
				}

			}
			localize.setEntryValue(targetEntity);
			// persist without performing any validations
			context.getCatalogManager().getWrite().execute(localize);
			targetEntity = localize.getEntryResult();

			o.setId(targetEntity.getId());
			context.setResults(Collections.singletonList(o));
		} else {
			/* DISTRIBUTED */
			return context.getCatalogManager().getNew().execute(context);
		}

		return CONTINUE_PROCESSING;
	}

}
