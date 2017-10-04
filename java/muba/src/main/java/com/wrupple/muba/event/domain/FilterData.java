package com.wrupple.muba.event.domain;

import java.util.List;

public interface FilterData {
	public final String EQUALS = "==";
	public final String GREATEREQUALS = ">=";
	public final String LESSEQUALS = "<=";
	public final String LESS = "<";
	public final String GREATER = ">";
	public final String DIFFERENT = "!=";
	public final String LIKE = "LIKE";
	public final String IN = "IN"; 
	public final String CONTAINS_EITHER = "HAS";
	public final String REGEX = "REGEX";
	public final String STARTS = "START";
	public final String ENDS = "END";

	/*
	 * Default config
	 */
	public final int DEFAULT_INCREMENT = 25;
	//public final int DEFAULT_STARTING_LENGTH = DEFAULT_INCREMENT;

	public int getStart();

	public void setStart(int start);

	public int getLength();
	
	public void setLength(int length);
	
	
	public void setConstrained(boolean constrained);

	public boolean isConstrained();

	// join result sets
	/*
	 * See how we are gona go about joining a foreign catalog
	 * [Prouctos][id][productos]
	 * [foreignCatalogId][foreignField][localField]
	 */
	public String[][] getJoins();

	public void setJoins(String[][] joins);

	// limit result sets

	public String[] getColumns();

	public void setColumns(String[] column);

	// order result entries

	public void setOrdering(List<? extends FilterDataOrdering> order);

	public List<? extends FilterDataOrdering> getOrdering();

	// filter criteria

	public List<? extends FilterCriteria> getFilters();

	public void addFilter(FilterCriteria criteria);

	public void removeFilterByValue(String idField, String valueToRemove);

	public FilterCriteria fetchCriteria(String idField);

	public boolean containsKey(String field);

	public void addOrdering(FilterDataOrdering ordering);

	/**
	 * @return most implementations will use the primary key of the last entity received to ask for the next batch
	 * start and end parameters may change meaning if paging key is available
	 */
	public String getCursor();
	
	void setCursor(String key);
	
	boolean isUnique();
	
	public void setUnique(boolean unique);

}
