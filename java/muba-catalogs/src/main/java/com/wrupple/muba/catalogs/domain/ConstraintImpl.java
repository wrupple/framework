package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.Constraint;

public class ConstraintImpl extends CatalogEntryImpl implements Constraint {
	private static final long serialVersionUID = -3579585044610207044L;
	private List<String> properties;
	private String distinguishedName;

	@Override
	public String getCatalogType() {
		return Constraint.CATALOG_ID;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}


}
