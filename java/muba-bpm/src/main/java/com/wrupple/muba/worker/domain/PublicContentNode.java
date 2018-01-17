package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ContentNode;

public interface PublicContentNode extends ContentNode {
	
	String getCatalogEntry();

	Long getDiscriminator();
	
	void setCatalogEntry(String id);

	void setDiscriminator(Long id);
	
	/**
	 * @return the Level Index in the Children Tree
	 */
    Long getChildrenTreeLevelIndex();

    Long getDiscriminatingIndex();

}
