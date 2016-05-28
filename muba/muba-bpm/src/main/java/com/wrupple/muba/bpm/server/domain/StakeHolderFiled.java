package com.wrupple.muba.bpm.server.domain;

import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.server.domain.FieldDescriptorImpl;

public class StakeHolderFiled extends FieldDescriptorImpl {

	private static final long serialVersionUID = -2836119157443551044L;
/*
 * FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		if(stakeHolderField!=null&&!stakeHolderField.isMultiple()&&stakeHolderField.getDataType()==CatalogEntry.INTEGER_DATA_TYPE){
		
 */
	
	public StakeHolderFiled() {
		makeKey(HasStakeHolder.STAKE_HOLDER_FIELD, "Stake Holder", Person.CATALOG, false);
		setWriteable(false);
	}
}
