package com.wrupple.vegetate.domain;

import java.util.List;

public interface CatalogTrigger extends HasCatalogId, HasStakeHolder,CatalogEntry,HasEntryId {
	
	String SOURCE_ENTRY = "entry";
	String SOURCE_CONTEXT = "context";
	String SOURCE_OLD = "old";
	String SERIALIZED = "serializedEntry";

	String OLD_ENTRY_CONTEXT_KEY = "trigger."+SOURCE_OLD;
	String NEW_ENTRY_CONTEXT_KEY = "trigger."+SOURCE_ENTRY;
	Object CATALOG_CONTEXT_KEY = "trigger.catalog";
	String getCatalogEntryId();
	String getCatalogId();
	// create,update,delete,evaluate
	public String getHandler();
	void setHandler(String h);
	
	public String getSeed();
	void setSeed(String s);
	
	
	public String getExpression();
	void setExpression(String s);
	
	public String getDescription();

	
	boolean isRunAsStakeHolder();
	void setRunAsStakeHolder(Boolean b);

	public List<String> getProperties();
	void setProperties(List<String> p);
	
	/**
	 * @return if true the entire chain of events that lead to this trigger failing should be rollbacked
	 */
	boolean isRollbackOnFail();
	void setRollbackOnFail(Boolean n);
	
	/**
	 * @return if true and this trigger fails, no further triggers will be excecuted
	 */
	boolean isStopOnFail();
	void setStopOnFail(Boolean b);
}
