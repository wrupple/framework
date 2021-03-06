package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

/**
 * 
 * delegates to whatever DAO the LocalizedEntity catalog uses to access data,
 * however, this implementation intercepts the creation of any entity pointing
 * to a catalog that uses
 * {@link com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin}
 * and updates/creates columns on that same entity, but for the given locale
 * 
 * @author japi
 *
 */
@Singleton
public class LocalizedEntityInterceptorDAO implements CatalogCreateTransaction {

	private final FieldAccessStrategy access;
	private final CatalogDescriptorService catalogService;

	@Inject
	public LocalizedEntityInterceptorDAO(FieldAccessStrategy access, CatalogDescriptorService catalogService) {
		super();
		this.access = access;
		this.catalogService = catalogService;
	}

	@Override
	public boolean execute(CatalogActionContext context) throws Exception {
		DistributiedLocalizedEntry o = (DistributiedLocalizedEntry) context.getRequest().getEntryValue();

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

		// what catalog is this localized entity pointing to?
        CatalogDescriptor pointsTo =context.triggerGet(CatalogDescriptor.CATALOG_ID,numericCatalogId);

		Instrospection instrospection = access.newSession(pointsTo);

		// what strategy does the referenced catalog use to localize it's
		// entities
		int strategy = pointsTo.getLocalization();
		if (0 == strategy /* CONSOLIDATED */) {
			// this is the special case we need to intercept
			String catalogId = pointsTo.getDistinguishedName();
			instrospection.resample(o);

			// read localized field values
			List<String> values = (List<String>) o.getProperties();

			// read localizable entity
			CatalogDescriptor localizedCatalog = catalogService.getDescriptorForName(catalogId,context);
			PersistentCatalogEntity targetEntity = context.triggerGet(catalogId,entryId);

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

			targetEntity = context.triggerWrite(catalogId,entryId,targetEntity);

			o.setId(targetEntity.getId());
			context.setResults(Collections.singletonList(o));
		} else {
			/* DISTRIBUTED */
			context.create(
					(String)context.getRequest().getCatalog(),
					(CatalogEntry)context.getRequest().getEntryValue()
			);
		}

		return CONTINUE_PROCESSING;
	}

}
