package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.desktop.server.service.FailedPermissionCheckRecovery;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.domain.FilterCriteriaImpl;
import com.wrupple.vegetate.server.domain.FilterDataImpl;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;

@Singleton
public class FailedPermissionCheckRecoveryImpl implements FailedPermissionCheckRecovery {

	@Override
	public boolean attemptToRecoverFromPermissionCheck(Context context, VegetateServiceManifest manifest) {
		if (CatalogServiceManifest.SERVICE_NAME.equals(manifest.getServiceName())) {
			CatalogExcecutionContext catalogContext = (CatalogExcecutionContext) context;
			String action = catalogContext.getAction();
			String catalog = catalogContext.getCatalog();
			if (CatalogActionRequest.READ_ACTION.equals(action)) {

				String entry = catalogContext.getEntry();
				FilterDataImpl filter;
				FilterCriteriaImpl criteria = new FilterCriteriaImpl();
				criteria.setValue(true);
				criteria.setOperator(FilterData.EQUALS);
				criteria.pushToPath(CatalogEntry.PUBLIC);
				if (entry == null) {
					filter = catalogContext.getFilter();
					filter.addFilter(criteria);
					catalogContext.setFilter(filter);
					return true;
				} else {
					filter = new FilterDataImpl();
					filter.setConstrained(true);
					filter.setStart(0);
					filter.setLength(1);
					filter.addFilter(criteria);
					criteria = new FilterCriteriaImpl();
					criteria.addValue(entry);
					criteria.setOperator(FilterData.EQUALS);
					criteria.pushToPath(CatalogEntry.ID_FIELD);
					filter.addFilter(criteria);
					catalogContext.setEntry(null);
					catalogContext.setFilter(filter);
					return true;
				}
			} else if (CatalogActionRequest.LIST_ACTION_TOKEN.equals(catalog)) {
				// TODO refine
				return true;
			}
		}
		return false;
	}

}
