package com.wrupple.muba.cms.shared.services;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.vegetate.domain.CatalogDescriptor;

public interface ContentManagerRegistry {

	/**
	 * @param desktopPlaceHierarchy
	 * @param catalog
	 * @param action
	 * @return the domain (or user) defined ApplicationItem used to managed the
	 *         specified action on the given content type ( Catalog ), or the
	 *         system defined default
	 */
	ApplicationItem getManager(ApplicationItem desktopPlaceHierarchy, CatalogDescriptor catalog, String action);

}
