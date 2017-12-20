package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.JSONObject;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.FilterDataOrdering;

import java.util.List;

public final class JsFilterData extends JavaScriptObject implements FilterData {

	protected JsFilterData() {
	}

	public static final JsFilterData newFilterData() {
		JavaScriptObject d = JavaScriptObject.createObject();
		JsFilterData regreso = d.cast();
		regreso.setConstrained(true);
		regreso.setStart(0);
		regreso.setLength(DEFAULT_INCREMENT);
		return regreso;
	}

	public static JsFilterData createSingleFieldFilter(String field, JsArrayMixed values) {
		if (field == null) {
			throw new IllegalArgumentException();
		}
		JsFilterData regreso = newFilterData();
		regreso.setConstrained(false);
		if (values == null || values.length() == 0) {
		} else {
			JsFilterCriteria product = JsFilterCriteria.newFilterCriteria();
			product.setOperator(FilterData.EQUALS);
			product.setValues(values);
			product.pushToPath(field);
			regreso.addFilter(product);
		}

		return regreso;
	}

	public static JsFilterData createSingleFieldFilter(String field, JsArrayString values) {
		if (values == null) {
			return null;
		}
		JsArrayMixed v = values.cast();
		return createSingleFieldFilter(field, v);
	}

	public static JsFilterData createSingleFieldFilter(String idField, String value) {
		if(value==null||value.isEmpty()){
			return null;
		}
		JsArrayString r = JsArrayString.createArray().cast();
		r.push(value);
		return createSingleFieldFilter(idField, r);
	}

	public static JsFilterData createSingleFieldFilter(String field, JsArrayNumber values) {
		JsFilterData regreso = newFilterData();
		regreso.setConstrained(false);
		if (values == null || values.length() == 0) {
		} else {
			JsFilterCriteria product = JsFilterCriteria.newFilterCriteria();
			product.setOperator(FilterData.EQUALS);
			product.setValues(values);
			product.pushToPath(field);
			regreso.addFilter(product);
		}
		return regreso;

	}

	public static JsFilterData createSingleFieldFilter(String idField, List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return newFilterData();
		} else {
			JsArrayString r = JsArrayString.createArray().cast();
			for (String s : ids) {
				r.push(s);
			}
			return createSingleFieldFilter(idField, r);
		}
	}

	public static JsFilterData copy(JsFilterData jFilter, boolean excluderange) {

		JsFilterData regreso = GWTUtils.eval(new JSONObject(jFilter).toString()).cast();

		if (excluderange) {
			regreso.setConstrained(false);
			regreso.setStart(0);
			regreso.setLength(DEFAULT_INCREMENT);
		}

		return regreso;
	}

	@Override
	public final native int getStart() /*-{
		if (this.start == null) {
			this.start = 0;
		}
		return this.start;
	}-*/;

	@Override
	public final native void setStart(int start) /*-{
		this.start = start;
	}-*/;

	@Override
	public final native int getLength() /*-{
		return this.length;
	}-*/;

	@Override
	public final native void setLength(int length) /*-{
		this.length = length;
	}-*/;

	@Override
	public final native void setConstrained(boolean constrained)/*-{
		this.constrained = constrained;
	}-*/;

	@Override
	public final native boolean isConstrained() /*-{
		return this.constrained;
	}-*/;

	public final native JsArray<JsArrayString> getJoinsArray(boolean create)/*-{
		if (this.joins == null && create) {
			this.joins = [];
		}
		return this.joins;
	}-*/;

	public final native JsArray<JsFilterDataOrdering> getOrderArray() /*-{
		if (this.order == null) {
			this.order = [];
		}
		return this.order;
	}-*/;

	public final native void setOrderArray(JsArray<JsFilterDataOrdering> o) /*-{
		this.order = o;
	}-*/;

	public final native void setFiltersArray(JsArray<JsFilterCriteria> arr) /*-{
		this.filters = arr;
	}-*/;

	@Override
	public native void addOrdering(FilterDataOrdering t) /*-{
		if (this.order == null) {
			this.order = [];
		}
		this.order.push(t);
	}-*/;

	@Override
	public final List<FilterDataOrdering> getOrdering() {
		List rergeso = JsArrayList.arrayAsList(getOrderArray());
		return rergeso;
	}

	@Override
	public final String[][] getJoins() {
		JsArray<JsArrayString> joins = getJoinsArray(false);
		if (joins == null) {
			return null;
		} else {
			joins = getJoinsArray(true);
			String[][] regreso = new String[joins.length()][];
			JsArrayString inner;
			for (int i = 0; i < joins.length(); i++) {
				inner = joins.get(i);
				regreso[i] = new String[inner.length()];
				for (int j = 0; j < inner.length(); j++) {
					regreso[i][j] = inner.get(j);
				}
			}
			return regreso;
		}
	}

	@Override
	public final void setJoins(String[][] jo) {
		if (jo == null) {
			GWTUtils.deleteAttribute(this, "joins");
		} else {
			JsArray<JsArrayString> joins = JavaScriptObject.createArray().cast();
			JsArrayString value;
			for (int i = 0; i < jo.length; i++) {
				value = JavaScriptObject.createArray().cast();
				joins.push(value);
				for (int j = 0; j < jo[i].length; j++) {
					value.push(jo[i][j]);
				}
			}
			setJoins(joins);
		}
	}

	private final JsArray<JsFilterCriteria> unwrap(List<FilterCriteria> value) {
		JsArray<JsFilterCriteria> regreso = JavaScriptObject.createArray().cast();
		for (FilterCriteria c : value) {
			regreso.push((JsFilterCriteria) c);
		}
		return regreso;
	}

	@Override
	public final JsFilterCriteria fetchCriteria(String idField) {
		JsArray<JsFilterCriteria> criteria = getFilterArray();
		JsFilterCriteria regreso = null;
		for (int i = 0; i < criteria.length(); i++) {
			if (idField.equals(criteria.get(i).getPath(0))) {
				regreso = criteria.get(i);
			}
		}
		return regreso;
	}
	
	

	public final native JsArray<JsFilterCriteria> getFilterArray() /*-{
		if (this.filters == null) {
			this.filters = [];
		}
		return this.filters;
	}-*/;

	@Override
	public final void removeFilterByValue(String idField, String valueToRemove) {
		JsArray<JsFilterCriteria> regreso = getFilterArray();
		JsFilterCriteria temp;
		for (int i = 0; i < regreso.length(); i++) {
			temp = regreso.get(i);
			temp.removeValue(valueToRemove);
		}
	}

	@Override
	public void setOrdering(List<? extends FilterDataOrdering> order) {
		if (order == null) {
			setOrderArray(null);
		} else {
			JsArray<JsFilterDataOrdering> arr = JsArrayList.unwrap((List<JsFilterDataOrdering>) order);
			setOrderArray(arr);
		}
	}

	public void setFilters(List<? extends FilterCriteria> filters) {
		JsArray<JsFilterCriteria> arr = JsArrayList.unwrap((List<JsFilterCriteria>) filters);
		setFiltersArray(arr);

	}

	@Override
	public void addFilter(FilterCriteria product) {
		JsArray<JsFilterCriteria> arr = getFilterArray();
		arr.push((JsFilterCriteria) product);
	}

	@Override
	public List<JsFilterCriteria> getFilters() {
		return JsArrayList.arrayAsList(getFilterArray());
	}

	@Override
	public boolean containsKey(String field) {
		return fetchCriteria(field) != null;
	}

	public native void clearEmpty() /*-{
		if (this.filters != null) {
			if (this.filters.length == 0) {
				this.filters = null;
			}
		}
		if (this.order != null) {
			if (this.order.length == 0) {
				this.order = null;
			}
		}
		if (this.joins != null) {
			if (this.joins.length == 0) {
				this.joins = null;
			}
		}
	}-*/;

	@Override
	public String[] getColumns() {
		JsArrayString columns = getColumnsArray();
		if (columns == null) {
			return null;
		} else {
			String[] regreso = new String[columns.length()];
			String inner;
			for (int i = 0; i < columns.length(); i++) {
				inner = columns.get(i);
				regreso[i] = inner;
			}
			return regreso;
		}
	}

	public native JsArrayString getColumnsArray() /*-{
		return this.columns;
	}-*/;

	@Override
	public void setColumns(String[] column) {
		if (column == null) {
			GWTUtils.deleteAttribute(this, "columns");
		} else {
			JsArrayString joins = JavaScriptObject.createArray().cast();
			String value;
			for (int i = 0; i < column.length; i++) {
				value = column[i];
				joins.push(value);
			}
			GWTUtils.setAttribute(this, "columns", joins);
		}
	}

	public void removeFilterByField(String field) {
		JsArray<JsFilterCriteria> criteria = getFilterArray();
		if (criteria != null) {
			JsFilterCriteria crit;
			String firstPathToken;
			int i = 0;
			while (i < criteria.length()) {
				crit = criteria.get(i);
				if (crit != null) {
					firstPathToken = crit.getPath(0);
					if (firstPathToken != null && firstPathToken.equals(field)) {
						splice(criteria, i);
					} else {
						i++;
					}
				}

			}
		}
	}

	private native void splice(JsArray<JsFilterCriteria> criteria, int index)/*-{
		criteria.splice(index, 1);
	}-*/;

	public void setJoins(JsArray<JsArrayString> joins) {
		GWTUtils.setAttribute(this, "joins", joins);
	}

	@Override
	public native String getCursor() /*-{
		return this.cursor;
	}-*/;

	@Override
	public native void setCursor(String cursor) /*-{
		this.cursor=cursor;
	}-*/;



}
