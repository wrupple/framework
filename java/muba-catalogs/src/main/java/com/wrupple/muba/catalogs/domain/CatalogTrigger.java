package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogIntent;
import com.wrupple.muba.event.domain.reserved.HasProperties;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

public interface CatalogTrigger extends CatalogIntent, HasStakeHolder,HasProperties {
	
	String SERIALIZED = "serializedEntry";

	String getEntry();
	String getCatalog();
	// create,update,delete,evaluate
	public String getHandler();
	void setHandler(String h);
	
	public String getSeed();
	void setSeed(String s);
	
	public String getDescription();

	
	boolean isRunAsStakeHolder();
	void setRunAsStakeHolder(Boolean b);

	
	/**
	 * @return if true the entire chain of events that lead to this trigger failing should be rollbacked
	 */
	boolean isFailSilence();
	void setFailSilence(Boolean n);
	
	/**
	 * @return if true and this trigger fails, no further triggers will be excecuted
	 */
	boolean isStopOnFail();
	void setStopOnFail(Boolean b);
}
