package com.wrupple.muba.worker.server.domain;

import com.wrupple.muba.catalogs.domain.UserDefinedCatalogActionConstraint;
import com.wrupple.muba.event.domain.ManagedObject;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;

import java.util.List;

public interface ValueChangeTrigger extends UserDefinedCatalogActionConstraint, HasCatalogId, ManagedObject, HasDistinguishedName {

    String CATALOG = "ValueChangeAudit";
	
	String getInitialValue();
	
	String getFinalValue();
	
	Integer getEncoding();

	List<String> getCommandChain();

}
