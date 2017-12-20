package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.catalogs.domain.UserDefinedCatalogActionConstraint;
import com.wrupple.muba.event.domain.ManagedObject;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasFieldId;

import java.util.List;

public interface ValueChangeTrigger extends UserDefinedCatalogActionConstraint, HasCatalogId, ManagedObject, HasFieldId {

    String CATALOG = "ValueChangeAudit";
	
	String getInitialValue();
	
	String getFinalValue();
	
	Integer getEncoding();

	List<String> getCommandChain();

}
