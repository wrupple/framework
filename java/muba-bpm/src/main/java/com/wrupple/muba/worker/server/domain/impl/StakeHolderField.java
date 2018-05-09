package com.wrupple.muba.worker.server.domain.impl;

import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

public class StakeHolderField extends FieldDescriptorImpl {

	private static final long serialVersionUID = -2836119157443551044L;
/*
 * FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		if(stakeHolderField!=null&&!stakeHolderField.isMultiple()&&stakeHolderField.getDataType()==CatalogEntry.INTEGER_DATA_TYPE){
		
 */
	
	public StakeHolderField() {
		makeKey(HasStakeHolder.STAKE_HOLDER_FIELD, "Stake Holder", Person.CATALOG, false);
		setWriteable(false);
	}
}
