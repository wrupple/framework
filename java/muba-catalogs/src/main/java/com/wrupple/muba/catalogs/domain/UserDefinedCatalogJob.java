package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

public interface UserDefinedCatalogJob extends HasStakeHolder,CatalogJob {
	
	String SERIALIZED = "serializedEntry";

	
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
