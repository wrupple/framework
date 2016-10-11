package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;
import com.wrupple.muba.catalogs.server.domain.FilterCriteriaImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;

public class FilterDataUtils {
	public static final FilterData filter = FilterDataUtils.newFilterData();
	
	public static FilterDataImpl newFilterData() {
		FilterDataImpl regreso;
		regreso = new FilterDataImpl();
		return regreso;
	}

	public static FilterCriteria newFilterCriteria() {
		return new FilterCriteriaImpl();
	}

	public static FilterDataOrdering newFilterDataOrdering() {
		return new FilterDataOrderingImpl();
	}
	public static FilterDataImpl createSingleFieldFilter(String field, long personId) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		FilterDataImpl regreso = newFilterData();
		regreso.setConstrained(false);
		FilterCriteria product = newFilterCriteria();
		product.setOperator(FilterData.EQUALS);
		product.setValue(personId);
		product.pushToPath(field);
		regreso.addFilter(product);
		return regreso;
	}
	public static FilterDataImpl createSingleFieldFilter(String field, double personId) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		FilterDataImpl regreso = newFilterData();
		regreso.setConstrained(false);
		FilterCriteria product = newFilterCriteria();
		product.setOperator(FilterData.EQUALS);
		product.setValue(personId);
		product.pushToPath(field);
		regreso.addFilter(product);
		return regreso;
	}
	public static FilterData createSingleFieldFilter(String field, String value) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		FilterData regreso = newFilterData();
		regreso.setConstrained(false);
		if (value == null || value.length() == 0) {
		} else {
			FilterCriteria product = newFilterCriteria();
			product.setOperator(FilterData.EQUALS);
			product.setValue(value);
			product.pushToPath(field);
			regreso.addFilter(product);
		}
		return regreso;
	}

	public static FilterData createSingleFieldFilter(String field, List<String> values) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		FilterData regreso = newFilterData();
		regreso.setConstrained(false);
		if (values == null || values.size() == 0) {
		} else {
			FilterCriteria product = newFilterCriteria();
			product.setOperator(FilterData.EQUALS);
			product.setValues(new ArrayList<Object>(values));
			product.pushToPath(field);
			regreso.addFilter(product);
		}
		return regreso;
	}

	public static FilterData createSingleKeyFieldFilter(String field, List<?> values) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		FilterData regreso = newFilterData();
		regreso.setConstrained(false);
		if (values == null || values.size() == 0) {
		} else {
			FilterCriteria product = newFilterCriteria();
			product.setOperator(FilterData.EQUALS);
			product.setValues(new ArrayList<Object>(values));
			product.pushToPath(field);
			regreso.addFilter(product);
		}
		return regreso;
	}

	public static FilterData copyData(FilterData filter, boolean excluderange) {

		FilterData regreso = newFilterData();
		regreso.setJoins(filter.getJoins());
		regreso.setOrdering((List<FilterDataOrdering>) filter.getOrdering());
		((FilterDataImpl) regreso).setFilters((List<? extends FilterCriteriaImpl>) filter.getFilters());
		return regreso;

	}

}
