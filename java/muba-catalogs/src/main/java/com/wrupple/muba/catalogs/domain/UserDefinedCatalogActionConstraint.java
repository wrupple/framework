package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionConstraint;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

public interface UserDefinedCatalogActionConstraint extends HasStakeHolder, CatalogActionConstraint {

    String SERIALIZED = "serializedEntry";

	
	Boolean getRunAsStakeHolder();
	void setRunAsStakeHolder(Boolean b);

	
	/**
	 * @return if true the entire chain of events that lead to this trigger failing should be rollbacked
	 */
	Boolean getFailSilence();
	void setFailSilence(Boolean n);
	
	/**
	 * @return if true and this trigger fails, no further triggers will be excecuted
	 */
	Boolean getStopOnFail();
	void setStopOnFail(Boolean b);
}
