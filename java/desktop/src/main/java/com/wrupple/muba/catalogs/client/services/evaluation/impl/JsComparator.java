package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import java.util.Comparator;

import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class JsComparator implements Comparator<JsCatalogEntry> {
	String field;
	boolean asc;

	public JsComparator(String field, boolean asc) {
		super();
		this.field = field;
		this.asc = asc;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public int compare(JsCatalogEntry o1, JsCatalogEntry o2) {
		return jsCompare(o1, o2, field, asc);
	}

	private native int jsCompare(JsCatalogEntry o1, JsCatalogEntry o2, String field, boolean asc) /*-{

		var value1 = o1[field];
		var value2 = o2[field];

		if (value1 == null || value2 == null) {
			if (value1 == null && value2 == null) {
				return 0;
			} else {
				if (value1 == null) {
					if (asc) {
						return -1;
					} else {
						return 1;
					}
				} else {
					if (asc) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		} else {
			if (value1 == value2) {
				return 0;
			} else {
				if (value1 > value2) {
					if (asc) {
						return 1;
					} else {
						return -1;
					}
				} else {
					if (asc) {
						return -1;
					} else {
						return 1;
					}
				}
			}
		}

	}-*/;

}