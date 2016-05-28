package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.domain.HasStakeHolder;

public interface BusinessRule extends ContentNode, HasStakeHolder,HasCatalogId,HasEntryId {

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
