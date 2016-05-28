package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.structure.HasTimestamp;

public interface ContentNode extends CatalogEntry, HasTimestamp {

	// FIXME RENAME CATALOG ID to CONTENT_NODE
	String CATALOG = "TimelineEvent";
	String CHILDREN_TREE_LEVEL_INDEX="childrenTreeLevelIndex";
	String DISCRIMINATING_INDEX = "discriminatingIndex";
	/*
	 * List<String> getDiscriminators();
	 * 
	 * void setCatalog(String catalog);
	 * 
	 * Long getNumericalCatalogId();
	 * 
	 * void setNumericalCatalogId(Long catalogId);
	 */
	
}
