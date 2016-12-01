package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.catalogs.domain.ContentNode;

public interface PublicContentNode extends ContentNode{
	
	String getCatalogEntry();

	Long getDiscriminator();
	
	void setCatalogEntry(String id);

	void setDiscriminator(Long id);
	
	/**
	 * @return the Level Index in the Children Tree
	 */
	public Long getChildrenTreeLevelIndex();
	
	public Long getDiscriminatingIndex();

}
