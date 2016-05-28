package com.wrupple.vegetate.domain;


public interface FilterDataOrdering {
	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending);
	/**
	 * @return the ascending
	 */
	public boolean isAscending();
	/**
	 * @param field the field to set
	 */
	public void setField(String field);
	/**
	 * @return the field
	 */
	public String getField();
}
