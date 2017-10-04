package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.CatalogDescriptor;

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
