package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.FilterData;

public class CatalogProcessDescriptor {

	private FilterData filterData;
	private String selectedType;
	private String selectedValueId;

	public CatalogProcessDescriptor(FilterData filterData, String catalog, String entryId) {
		super();
		this.filterData = filterData;
		this.selectedType = catalog;
		this.selectedValueId = entryId;
	}

	public CatalogProcessDescriptor() {
		super();
	}

	public FilterData getFilterData() {
		return filterData;
	}

	public void setFilterData(FilterData filterData) {
		this.filterData = filterData;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String catalog) {
		this.selectedType = catalog;
	}

	public String getSelectedValueId() {
		return selectedValueId;
	}

	public void setSelectedValueIdd(String entryId) {
		this.selectedValueId = entryId;
	}

}
