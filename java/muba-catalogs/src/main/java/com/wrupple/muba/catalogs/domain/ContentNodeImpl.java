package com.wrupple.muba.catalogs.domain;

import java.util.Date;

import com.wrupple.muba.event.domain.CatalogEntryImpl;

public class ContentNodeImpl extends CatalogEntryImpl implements ContentNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8359689701658671580L;
	private Date timestamp;

	@Override
	public String getCatalogType() {
		return CATALOG;
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
