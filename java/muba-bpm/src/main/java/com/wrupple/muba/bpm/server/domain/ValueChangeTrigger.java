package com.wrupple.muba.bpm.server.domain;

import java.util.List;

import com.wrupple.muba.catalogs.domain.UserDefinedCatalogJob;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasFieldId;
import com.wrupple.muba.event.domain.ManagedObject;

public interface ValueChangeTrigger extends UserDefinedCatalogJob,HasCatalogId ,ManagedObject,HasFieldId{
	
	String CATALOG = "ValueChangeAudit";
	
	String getInitialValue();
	
	String getFinalValue();
	
	Integer getEncoding();

	List<String> getCommandChain();

}
