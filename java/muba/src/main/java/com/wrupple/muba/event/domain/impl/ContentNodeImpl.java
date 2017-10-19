package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.ContentNode;

import java.util.Date;

public class ContentNodeImpl extends CatalogEntryImpl implements ContentNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8359689701658671580L;
	private Date timestamp;

	@Override
	public String getCatalogType() {
		return ContentNode.CATALOG_TIMELINE;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date d) {
		this.timestamp=d;
	}


}
