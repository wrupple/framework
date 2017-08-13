package com.wrupple.muba.bpm.server.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasFieldId;
import com.wrupple.muba.bpm.domain.ManagedObject;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;

public interface ValueChangeTrigger extends CatalogTrigger,HasCatalogId ,ManagedObject,HasFieldId{
	
	String CATALOG = "ValueChangeAudit";
	
	String getInitialValue();
	
	String getFinalValue();
	
	Integer getEncoding();

	List<String> getCommandChain();

}
