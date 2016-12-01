package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.catalogs.domain.ContentNode;

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
