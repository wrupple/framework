package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.event.domain.ContentNode;
import com.wrupple.muba.event.domain.reserved.HasCatalogKey;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

/*
 * as a business rule, user are granted or denied permission to change value from a specific initial value
 */
public interface BusinessRule extends ContentNode, HasStakeHolder,HasCatalogKey{

	String CATALOG = "BusinessRule";
	
	Long getCatalogNumericId();
	
	String getField();
	
	String getFieldFinalValue();
	
	String getFieldInitialValue();
	
	/*
	 * TO WHOM IS THIS RULE APPLIED TO
	 */
	
	String getRole();
	
	Long getPersonId();
	
	List<Long> getPeople();
}
