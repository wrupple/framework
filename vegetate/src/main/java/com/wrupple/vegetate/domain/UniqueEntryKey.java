package com.wrupple.vegetate.domain;

import java.io.Serializable;

public class UniqueEntryKey implements Serializable{
	private static final long serialVersionUID = 6857577233548771845L;
	private String catalogId;
	private String objectId;

	public UniqueEntryKey(String catalogId, String objectId) {
		super();
		this.catalogId = catalogId;
		this.objectId = objectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catalogId == null) ? 0 : catalogId.hashCode());
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniqueEntryKey other = (UniqueEntryKey) obj;
		if (catalogId == null) {
			if (other.catalogId != null)
				return false;
		} else if (!catalogId.equals(other.catalogId))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		return true;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public String getObjectId() {
		return objectId;
	}


}
