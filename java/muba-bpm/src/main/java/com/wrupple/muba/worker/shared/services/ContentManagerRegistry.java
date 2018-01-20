package com.wrupple.muba.worker.shared.services;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Workflow;

public interface ContentManagerRegistry {

	/**
	 * @param desktopPlaceHierarchy
	 * @param catalog
	 * @param action
	 * @return the domain (or user) defined Workflow used to managed the
	 *         specified action on the given content type ( Catalog ), or the
	 *         system defined default
	 */
	Workflow getManager(Workflow desktopPlaceHierarchy, CatalogDescriptor catalog, String action);

}
