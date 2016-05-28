package com.wrupple.vegetate.server.domain;

import java.io.Serializable;

import com.wrupple.vegetate.domain.FilterDataOrdering;

public class FilterDataOrderingImpl implements Serializable, FilterDataOrdering{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5872318262956903801L;
	private String field;
	private boolean ascending;
	
	public FilterDataOrderingImpl(String field, boolean ascending) {
		super();
		this.field = field;
		this.ascending = ascending;
	}
	public FilterDataOrderingImpl() {
		super();
	}
	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	@Override
	public String toString() {
		return "{\"field\":" + field ==null ? "null":"\""+field+"\""+ ", \"ascending\":" + ascending + "}";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result + ((field == null) ? 0 : field.hashCode());
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
		FilterDataOrderingImpl other = (FilterDataOrderingImpl) obj;
		if (ascending != other.ascending)
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}
	
}
