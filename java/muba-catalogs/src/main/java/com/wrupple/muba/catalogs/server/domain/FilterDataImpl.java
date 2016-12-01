package com.wrupple.muba.catalogs.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;

public class FilterDataImpl implements Serializable, FilterData {

	private static final long serialVersionUID = 228729890736454589L;
	private List<FilterCriteriaImpl> filters;
	private List<FilterDataOrderingImpl> order;
	private String[][] joins;
	private String[] columns;
	// COLUMN ALIAS?
	private int start = 0;
	private int length = DEFAULT_INCREMENT;
	private boolean constrained =true,unique;
	private String cursor;

	public FilterDataImpl() {
		super();
		filters = new ArrayList<FilterCriteriaImpl>(2);
	}

	public FilterDataImpl(boolean constrained) {
		this.constrained = constrained;
	}

	public FilterDataImpl(List<FilterCriteriaImpl> filters, ArrayList<FilterDataOrderingImpl> order2) {
		super();
		this.filters = filters;
		order = order2;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public List<FilterCriteriaImpl> getFilters() {
		return filters;
	}

	public List<FilterDataOrderingImpl> getOrder() {
		return order;
	}

	public void setOrder(List<FilterDataOrderingImpl> order) {
		this.order = order;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @param constrained
	 *            the constrained to set
	 */
	public void setConstrained(boolean constrained) {
		this.constrained = constrained;
	}

	/**
	 * @return the constrained
	 */
	public boolean isConstrained() {
		return constrained;
	}

	/**
	 * @return the order
	 */
	@Override
	public List<FilterDataOrderingImpl> getOrdering() {
		if (order == null) {
			return new ArrayList<FilterDataOrderingImpl>(2);
		}
		return order;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (constrained ? 1231 : 1237);
		result = prime * result + ((cursor == null) ? 0 : cursor.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + Arrays.deepHashCode(joins);
		result = prime * result + length;
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + start;
		result = prime * result + (unique ? 1231 : 1237);
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
		FilterDataImpl other = (FilterDataImpl) obj;
		if (constrained != other.constrained)
			return false;
		if (cursor == null) {
			if (other.cursor != null)
				return false;
		} else if (!cursor.equals(other.cursor))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (!Arrays.deepEquals(joins, other.joins))
			return false;
		if (length != other.length)
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (start != other.start)
			return false;
		if (unique != other.unique)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FilterDataImpl [filters=" + filters + ", order=" + order + ", joins=" + Arrays.toString(joins) + ", columns=" + Arrays.toString(columns)
				+ ", start=" + start + ", length=" + length + ", constrained=" + constrained + "]";
	}

	public String[][] getJoins() {
		return joins;
	}

	public void setJoins(String[][] joins) {
		this.joins = joins;
	}

	@Override
	public void removeFilterByValue(String idField, String valueToRemove) {
		FilterCriteria criteria = this.fetchCriteria(idField);
		if (criteria != null) {
			if (valueToRemove == null) {
				filters.remove(criteria);
			} else {
				criteria.removeValue(valueToRemove);
			}
		}
	}

	@Override
	public FilterCriteriaImpl fetchCriteria(String idField) {
		FilterCriteriaImpl criteria = null;
		for (FilterCriteriaImpl curr : filters) {
			if (idField.equals(curr.getPath(0))) {
				criteria = curr;
			}
		}

		return criteria;
	}

	@Override
	public void addFilter(FilterCriteria product) {
		if (filters == null) {
			filters = new ArrayList<FilterCriteriaImpl>();
		}
		if (product != null) {
			filters.add((FilterCriteriaImpl) product);
		}
	}

	@Override
	public void setOrdering(List<? extends FilterDataOrdering> order) {
		this.order = (List<FilterDataOrderingImpl>) order;
	}

	public void setFilters(List<? extends FilterCriteriaImpl> filters) {
		this.filters = (List<FilterCriteriaImpl>) filters;
	}

	@Override
	public boolean containsKey(String field) {
		return fetchCriteria(field) != null;
	}

	@Override
	public String[] getColumns() {
		return this.columns;
	}

	@Override
	public void setColumns(String[] column) {
		this.columns = column;
	}

	@Override
	public void addOrdering(FilterDataOrdering timestamp) {
		if (this.order == null) {
			this.order = new ArrayList<FilterDataOrderingImpl>();
		}
		this.order.add((FilterDataOrderingImpl) timestamp);
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String pagingKey) {
		this.cursor = pagingKey;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}


}
