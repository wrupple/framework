package com.wrupple.muba.bpm.server.domain;

import java.util.List;

import com.wrupple.muba.bpm.domain.ManagedObject;
import com.wrupple.vegetate.domain.CatalogTrigger;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasFieldId;

public interface ValueChangeTrigger extends CatalogTrigger,HasCatalogId ,ManagedObject,HasFieldId{
	
	String CATALOG = "ValueChangeTrigger";
	
	String getInitialValue();
	
	String getFinalValue();
	
	Integer getEncoding();

	List<String> getCommandChain();

}
