package com.wrupple.muba.event.domain.reserved;

public interface HasStakeHolder {
	
	String STAKE_HOLDER_FIELD = "stakeHolder";

	Object getStakeHolder();
	
	void setStakeHolder(Long stakeHolder);

}
