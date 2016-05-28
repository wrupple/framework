package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.VolatileCatalogDataAccessObject;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

public class VolatileCatalogDataAccessObjectImpl implements VolatileCatalogDataAccessObject<CatalogEntry> {
	private static final FilterData filter = FilterDataUtils.newFilterData();
	private final CatalogResultCache cache;
	private final CatalogDescriptor catalog;
	private CatalogExcecutionContext context;

	@Inject
	public VolatileCatalogDataAccessObjectImpl(CatalogResultCache cache, CatalogDescriptor catalog) {
		super();
		this.cache = cache;
		this.catalog = catalog;
	}

	@Override
	public List<CatalogEntry> read(FilterData filterData) throws Exception {
		return cache.satisfy(filterData);
	}

	@Override
	public CatalogEntry read(Object targetEntryId) throws Exception {
		return cache.get(targetEntryId);
	}

	@Override
	public CatalogEntry update(CatalogEntry originalEntry, CatalogEntry updatedEntry) throws Exception {
		 cache.update(originalEntry, updatedEntry);
		 return updatedEntry;
	}

	@Override
	public CatalogEntry create(CatalogEntry o) throws Exception {
		cache.append(o, filter);
		return o;
	}

	@Override
	public CatalogEntry delete(CatalogEntry o) throws Exception {
		cache.shiftRemove(o, filter);
		return o;
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {
		this.context=context;
		cache.init(context, catalog.getCatalogId());
	}

	@Override
	public CatalogExcecutionContext getContext() {
		return context;
	}

	@Override
	public void beginTransaction() throws NotSupportedException, SystemException {
		
	}

	@Override
	public void commitTransaction()
			throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		
	}

	@Override
	public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		
	}

}
