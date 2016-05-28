package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.PersistentCatalogEntityDAO;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.chain.command.I18nProcessing.DistributiedLocalizedEntry;

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
public class LocalizedEntityInterceptorDAO implements CatalogDataAccessObject<DistributiedLocalizedEntry> {

	private final CatalogDataAccessObject<DistributiedLocalizedEntry> cloudDistributedDao;
	private final CatalogPropertyAccesor accessor;

	public LocalizedEntityInterceptorDAO(CatalogDataAccessObject<DistributiedLocalizedEntry> delegate, CatalogPropertyAccesor accessor) {
		super();
		this.cloudDistributedDao = delegate;
		this.accessor = accessor;
	}

	@Override
	public DistributiedLocalizedEntry create(DistributiedLocalizedEntry o) throws Exception {

		// check all required data is present
		Long numericCatalogId = o.getCatalogId();
		Long entryId = o.getCatalogEntryId();
		String locale = o.getLocale();
		if (numericCatalogId == null || entryId == null || locale == null) {
			throw new IllegalArgumentException("Attempt to create a localized entity pointing to no existing catalog entry");
		}
		// format locale to be prepended to a field (looks like field_locale)
		locale = "_" + locale;

		CatalogExcecutionContext localize = getContext().getRequest().getStorageManager().spawn(getContext());

		localize.setEntry(numericCatalogId);
		localize.setFilter(null);
		localize.setCatalog(CatalogDescriptor.CATALOG_ID);
		// what catalog is this localized entity pointing to?
		getContext().getRequest().getStorageManager().getRead().execute(localize);
		CatalogDescriptor pointsTo = localize.getResult();
		Session session = accessor.newSession(pointsTo);

		// what strategy does the referenced catalog use to localize it's
		// entities
		int strategy = pointsTo.getLocalization();
		if (0 == strategy /* CONSOLIDATED */) {
			// this is the special case we need to intercept
			String catalogId = pointsTo.getCatalogId();
			session.resample(o);

			// read localized field values
			List<String> values = (List<String>) o.getProperties();

			// read localizable entity
			localize.setEntry(entryId);
			localize.setFilter(null);
			localize.setCatalog(catalogId);
			CatalogDescriptor localizedCatalog=localize.getCatalogDescriptor();
			getContext().getRequest().getStorageManager().getRead().execute(localize);
			PersistentCatalogEntity targetEntity= localize.getResult();

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

					targetEntity.setPropertyValue(value, new StringBuilder(fieldId.length() + locale.length()).append(fieldId).append(locale).toString());
				}

			}
			localize.setEntryValue(targetEntity);
			localize.put(PersistentCatalogEntityDAO.FORCE_WRITE, true);
			// persist without performing any validations
			getContext().getRequest().getStorageManager().getWrite().execute(localize);
			 targetEntity= localize.getResult();

			o.setIdAsString(targetEntity.getIdAsString());
			return o;
		} else {
			return cloudDistributedDao.create(o);
		}
	}

	@Override
	public void beginTransaction() throws NotSupportedException, SystemException {
		cloudDistributedDao.beginTransaction();
	}

	@Override
	public void commitTransaction()
			throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		cloudDistributedDao.commitTransaction();
	}

	@Override
	public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		cloudDistributedDao.rollbackTransaction();
	}

	@Override
	public List<DistributiedLocalizedEntry> read(FilterData filterData) throws Exception {
		return cloudDistributedDao.read(filterData);
	}

	@Override
	public DistributiedLocalizedEntry update(DistributiedLocalizedEntry originalEntry, DistributiedLocalizedEntry updatedEntry) throws Exception {
		return cloudDistributedDao.update(originalEntry, updatedEntry);
	}

	@Override
	public DistributiedLocalizedEntry delete(DistributiedLocalizedEntry o) throws Exception {
		return cloudDistributedDao.delete(o);
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {
		cloudDistributedDao.setContext(context);
	}

	@Override
	public CatalogExcecutionContext getContext() {
		return cloudDistributedDao.getContext();
	}

	@Override
	public DistributiedLocalizedEntry read(Object targetEntryId) throws Exception {
		return cloudDistributedDao.read(targetEntryId);
	}

}
