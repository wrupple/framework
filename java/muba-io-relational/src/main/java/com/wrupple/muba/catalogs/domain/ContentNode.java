package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasTimestamp;

public interface ContentNode extends CatalogEntry, HasTimestamp {

	// FIXME RENAME NUMERIC_ID ID to CONTENT_NODE
	Long NUMERIC_ID = -1l;
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